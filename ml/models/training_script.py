import pandas as pd
import numpy as np
from sklearn.model_selection import train_test_split
from transformers import (
    AutoTokenizer,
    AutoModelForSequenceClassification,
    TrainingArguments,
    Trainer
)
from datasets import Dataset
import torch
from sklearn.metrics import accuracy_score, f1_score
import os
from pathlib import Path

# Constants
MOOD_EMOJI_MAP = {
    'happiness': '😊',
    'sadness': '😢',
    'anger': '😠',
    'love': '❤️',
    'surprise': '😲',
    'fun': '😄',
    'hate': '👎',
    'neutral': '😐',
    'worry': '😟',
    'boredom': '🥱',
    'relief': '😌',
    'enthusiasm': '🤩',
    'empty': '◻️'
}
MODEL_NAME = "distilbert-base-uncased"
MODEL_SAVE_PATH = "../models/mood_model"

def load_and_clean_data(filepath):
    df = pd.read_csv(filepath)
    
    def clean_emotion(label):
        if '%' in str(label):
            return str(label).split('%')[0].split()[-1].lower()
        return str(label).lower().strip()
    
    df['Emotion'] = df['Emotion'].apply(clean_emotion)
    valid_emotions = set(MOOD_EMOJI_MAP.keys())
    df = df[df['Emotion'].isin(valid_emotions)]
    return df

def prepare_datasets(df):
    train_df, test_df = train_test_split(df, test_size=0.2, random_state=42)
    
    # Convert emotion labels to numerical IDs
    label2id = {label: i for i, label in enumerate(MOOD_EMOJI_MAP.keys())}
    train_df['label'] = train_df['Emotion'].map(label2id)
    test_df['label'] = test_df['Emotion'].map(label2id)
    
    train_dataset = Dataset.from_pandas(train_df)
    test_dataset = Dataset.from_pandas(test_df)
    
    tokenizer = AutoTokenizer.from_pretrained(MODEL_NAME)
    
    def tokenize_function(examples):
        return tokenizer(examples["text"], padding="max_length", truncation=True)
    
    tokenized_train = train_dataset.map(tokenize_function, batched=True)
    tokenized_test = test_dataset.map(tokenize_function, batched=True)
    
    # Set format for PyTorch
    tokenized_train.set_format("torch", columns=["input_ids", "attention_mask", "label"])
    tokenized_test.set_format("torch", columns=["input_ids", "attention_mask", "label"])
    
    return tokenized_train, tokenized_test, tokenizer

def compute_metrics(eval_pred):
    logits, labels = eval_pred
    predictions = np.argmax(logits, axis=-1)
    return {
        'accuracy': accuracy_score(labels, predictions),
        'f1': f1_score(labels, predictions, average='weighted')
    }


def train_model():
    df = load_and_clean_data(Path("data/emotion_sentiment_dataset.csv") )
    tokenized_train, tokenized_test, tokenizer = prepare_datasets(df)
    
    model = AutoModelForSequenceClassification.from_pretrained(
        MODEL_NAME,
        num_labels=len(MOOD_EMOJI_MAP),
        id2label={i: label for i, label in enumerate(MOOD_EMOJI_MAP.keys())},
        label2id={label: i for i, label in enumerate(MOOD_EMOJI_MAP.keys())},
        problem_type="single_label_classification"  # Explicitly specify problem type
    )
        
    training_args = TrainingArguments(
        output_dir="./results",
        eval_strategy="epoch",
        learning_rate=2e-5,
        per_device_train_batch_size=8,  # Reduced from 16 if you're having memory issues
        per_device_eval_batch_size=8,
        num_train_epochs=3,
        weight_decay=0.01,
        save_strategy="epoch",
        load_best_model_at_end=True,
        logging_dir='./logs',  # Added for better tracking
        logging_steps=500,
        report_to="none"  # Disables external logging services
    )
    
    trainer = Trainer(
        model=model,
        args=training_args,
        train_dataset=tokenized_train,
        eval_dataset=tokenized_test,
        compute_metrics=compute_metrics,
    )
    
    trainer.train()
    
    # Save model
    if not os.path.exists(MODEL_SAVE_PATH):
        os.makedirs(MODEL_SAVE_PATH)
    model.save_pretrained(MODEL_SAVE_PATH)
    tokenizer.save_pretrained(MODEL_SAVE_PATH)
    
    return model, tokenizer

if __name__ == "__main__":
    train_model()
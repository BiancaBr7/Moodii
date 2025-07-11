"""
THIS FILE SHOULD NOT BE RAN IN CASUAL CIRCUMSTANCES

The purpose of this file is to teach a model to predict emotions from text
Note: Run this file within the terminal to train a new model (This will take an absurd about of time). 

      This file should not be ran unless one wants to change the trained model, meaning this file should
      not be included within Docker and isn't even needed anymore after the model has completed training.

Author: Alyssa Dong
"""

import pandas as pd
import numpy as np
from sklearn.model_selection import train_test_split
from transformers import (
    AutoTokenizer,
    AutoModelForSequenceClassification,
    TrainingArguments,
    Trainer,
    DataCollatorWithPadding
)
from datasets import Dataset
import torch
import torch.nn as nn
from sklearn.metrics import accuracy_score, f1_score
import time
from pathlib import Path

# Constants

"""
training_script never uses the values for this dict only the keys and 
the length of the dict. 
(if changes are ONLY made to the values of the dict, there is no need to 
retrain the model)
"""
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

MODEL_NAME = "google/mobilebert-uncased"  # Faster alternative
MODEL_SAVE_PATH = "mood_model"
DEVICE = torch.device("cuda" if torch.cuda.is_available() else "cpu")

# Enable hardware optimizations
torch.backends.cudnn.benchmark = True
torch.backends.cuda.matmul.allow_tf32 = True

# Customizing a trainer
class MoodTrainer(Trainer):
    def compute_loss(self, model, inputs, return_outputs=False, num_items_in_batch=None):
        labels = inputs.pop("labels")
        outputs = model(**inputs)
        logits = outputs.logits
        loss_fct = nn.CrossEntropyLoss()
        loss = loss_fct(logits.view(-1, self.model.config.num_labels), labels.view(-1))
        return (loss, outputs) if return_outputs else loss

# Data Preparation
def load_and_clean_data(filepath):
    df = pd.read_csv(filepath)
    
    # Handle different column names
    emotion_col = next((col for col in df.columns if 'emotion' in col.lower()), 'emotion')
    if emotion_col not in df.columns:
        raise ValueError(f"Emotion column not found. Available columns: {df.columns.tolist()}")
    
    def clean_emotion(label):
        if pd.isna(label): return 'neutral'
        if '%' in str(label): return str(label).split('%')[0].split()[-1].lower()
        return str(label).lower().strip()
    
    df['emotion'] = df[emotion_col].apply(clean_emotion)
    valid_emotions = set(MOOD_EMOJI_MAP.keys())
    df = df[df['emotion'].isin(valid_emotions)]
    
    print("Emotion distribution:\n", df['emotion'].value_counts())
    return df

def prepare_datasets(df):
    # 80-20 split: 80% of the dataset goes to training, 20% of the dataset foes to testing
    train_df, test_df = train_test_split(df, test_size=0.2, random_state=42)
    
    # Convert labels to IDs
    label2id = {label: i for i, label in enumerate(MOOD_EMOJI_MAP.keys())}
    train_df['labels'] = train_df['emotion'].map(label2id)
    test_df['labels'] = test_df['emotion'].map(label2id)
    
    # Create datasets - only keep text and labels (does not include ids)
    train_dataset = Dataset.from_dict({
        "text": train_df["text"].tolist(),
        "labels": train_df["labels"].tolist()
    })
    test_dataset = Dataset.from_dict({
        "text": test_df["text"].tolist(),
        "labels": test_df["labels"].tolist()
    })
    
    # Tokenize with proper padding/truncation
    tokenizer = AutoTokenizer.from_pretrained(MODEL_NAME, use_fast=True)
    
    def tokenize_function(examples):
        return tokenizer(
            examples["text"],
            padding="max_length",
            truncation=True,
            max_length=128
        )
    
    tokenized_train = train_dataset.map(
        tokenize_function,
        batched=True,
        remove_columns=["text"],  # Only remove text column after tokenization
        batch_size=1024,
        num_proc=4
    )
    
    tokenized_test = test_dataset.map(
        tokenize_function,
        batched=True,
        remove_columns=["text"],
        batch_size=1024,
        num_proc=4
    )
    
    return tokenized_train, tokenized_test, tokenizer

def compute_metrics(eval_pred):
    logits, labels = eval_pred
    predictions = np.argmax(logits, axis=-1)
    return {
        'accuracy': accuracy_score(labels, predictions),
        'f1': f1_score(labels, predictions, average='weighted')
    }

def train_model():
    print(f"Using device: {DEVICE}")
    data_path = Path("data/emotion_sentiment_dataset.csv")
    df = load_and_clean_data(data_path)
    tokenized_train, tokenized_test, tokenizer = prepare_datasets(df)

    print("Columns in training dataset:", tokenized_train.column_names)
    print("Sample item:", {k: v for k, v in tokenized_train[0].items() if k != 'input_ids'})
    
    # Load model with optimizations
    model = AutoModelForSequenceClassification.from_pretrained(
        MODEL_NAME,
        num_labels=len(MOOD_EMOJI_MAP),
        id2label={i: l for i, l in enumerate(MOOD_EMOJI_MAP.keys())},
        label2id={l: i for i, l in enumerate(MOOD_EMOJI_MAP.keys())}
    ).to(DEVICE)

    # Training setup
    training_args = TrainingArguments(
        output_dir="./results",
        per_device_train_batch_size=16,  # Reduced from 32 so the model trains faster
        per_device_eval_batch_size=32,
        learning_rate=3e-5,
        num_train_epochs=2,
        weight_decay=0.01,
        eval_strategy="steps",
        eval_steps=500,
        save_strategy="steps",
        save_steps=500,
        logging_steps=100,
        fp16=torch.cuda.is_available(),  # Only enable if GPU available
        gradient_accumulation_steps=1,
        optim="adamw_torch",
        report_to="none",
        remove_unused_columns=False  # Important fix
    )
    
    trainer = MoodTrainer(
        model=model,
        args=training_args,
        train_dataset=tokenized_train,
        eval_dataset=tokenized_test,
        compute_metrics=compute_metrics,
        data_collator=DataCollatorWithPadding(tokenizer),
        tokenizer=tokenizer
    )
    
    print("Sample training example:", tokenized_train[0])
    assert 'labels' in tokenized_train.features, "Labels not found in dataset"

    torch.cuda.empty_cache()

    # Train with timing
    print("Starting training...")
    start_time = time.time()
    trainer.train()
    print(f"Training completed in {(time.time()-start_time)/60:.2f} minutes")
    
    # Save optimized model
    model.save_pretrained(MODEL_SAVE_PATH)
    tokenizer.save_pretrained(MODEL_SAVE_PATH)
    print(f"Model saved to {MODEL_SAVE_PATH}")

if __name__ == "__main__":
    train_model()
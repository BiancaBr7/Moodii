from transformers import AutoTokenizer, AutoModelForSequenceClassification
from dataset_loader import load_and_merge_datasets
import torch

def train_model():
    # Load merged dataset
    dataset = load_and_merge_datasets()
    
    # Initialize tokenizer and model
    tokenizer = AutoTokenizer.from_pretrained("bert-base-uncased")
    model = AutoModelForSequenceClassification.from_pretrained(
        "bert-base-uncased",
        num_labels=len(dataset["train"].unique("label"))
    )
    
    # Tokenize dataset
    def tokenize_fn(examples):
        return tokenizer(examples["text"], padding="max_length", truncation=True)
    
    tokenized_ds = dataset.map(tokenize_fn, batched=True)
    
    # Training setup
    training_args = TrainingArguments(
        output_dir="./results",
        per_device_train_batch_size=8,
        num_train_epochs=3
    )
    
    trainer = Trainer(
        model=model,
        args=training_args,
        train_dataset=tokenized_ds["train"]
    )
    
    trainer.train()
    model.save_pretrained("./models/text_mood")
    tokenizer.save_pretrained("./models/text_mood")
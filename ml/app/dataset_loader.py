from datasets import load_dataset, DatasetDict
import kagglehub
from kagglehub import KaggleDatasetAdapter
import pandas as pd

def load_and_merge_datasets():
    # Dataset 1: Emotion detection from text
    ds1 = kagglehub.load_dataset(
        KaggleDatasetAdapter.HUGGING_FACE,
        "pashupatigupta/emotion-detection-from-text",
        "datasets/emotion1.csv"  # Adjust based on actual file
    ).to_pandas()
    ds1 = ds1.rename(columns={"content": "text", "sentiment": "label"})
    
    # Dataset 2: Emotion analysis
    ds2 = kagglehub.load_dataset(
        KaggleDatasetAdapter.HUGGING_FACE,
        "simaanjali/emotion-analysis-based-on-text",
        "datasets/emotion2.csv"  # Adjust based on actual file
    ).to_pandas()
    ds2 = ds2.rename(columns={"text": "text", "emotion": "label"})
    
    # Dataset 3: MTEb emotion
    ds3 = load_dataset("mteb/emotion")
    ds3 = ds3["train"].to_pandas()
    ds3 = ds3.rename(columns={"label_text": "label"})
    
    # Standardize labels across datasets
    label_mapping = {
        "happy": "positive",
        "joy": "positive",
        "sadness": "negative",
        "anger": "negative",
        # Add all your label mappings
    }
    
    for df in [ds1, ds2, ds3]:
        df["label"] = df["label"].str.lower().map(label_mapping).fillna("neutral")
    
    # Combine datasets
    combined = pd.concat([ds1[["text", "label"]], 
                         ds2[["text", "label"]],
                         ds3[["text", "label"]]])
    
    return DatasetDict({
        "train": Dataset.from_pandas(combined)
    })
import pandas as pd
import numpy as np
from sklearn.model_selection import train_test_split
from sklearn.preprocessing import LabelEncoder
from sklearn.ensemble import RandomForestClassifier
from sklearn.metrics import classification_report
import joblib
import os
import sys

# Get the absolute path to the project root directory
PROJECT_ROOT = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))

def load_processed_data(data_dir):
    """Load processed features and labels with error handling"""
    try:
        features_path = os.path.join(data_dir, 'features.csv')
        labels_path = os.path.join(data_dir, 'labels.csv')
        
        if not os.path.exists(features_path) or not os.path.exists(labels_path):
            raise FileNotFoundError("Processed data files not found. Run data_processing.py first.")
            
        features = pd.read_csv(features_path)
        labels = pd.read_csv(labels_path)
        return features.values, labels['mood'].values
        
    except Exception as e:
        print(f"Error loading processed data: {str(e)}")
        sys.exit(1)

def train_model():
    # Use absolute paths
    data_dir = os.path.join(PROJECT_ROOT, 'processed_data')
    model_dir = os.path.join(PROJECT_ROOT, 'models')
    
    # Create models directory if it doesn't exist
    os.makedirs(model_dir, exist_ok=True)
    
    # Load data
    print("Loading processed data...")
    X, y = load_processed_data(data_dir)
    
    # Check if data was loaded
    if X.size == 0 or y.size == 0:
        print("Error: No data loaded. Check if data_processing.py ran successfully.")
        sys.exit(1)
    
    # Encode labels
    print("Encoding labels...")
    label_encoder = LabelEncoder()
    y_encoded = label_encoder.fit_transform(y)
    
    # Split data
    print("Splitting data...")
    X_train, X_test, y_train, y_test = train_test_split(
        X, y_encoded, test_size=0.2, random_state=42
    )
    
    # Train model
    print("Training model...")
    model = RandomForestClassifier(
        n_estimators=200,
        random_state=42,
        class_weight='balanced',  # Helps with imbalanced classes
        n_jobs=-1  # Use all available cores
    )
    model.fit(X_train, y_train)
    
    # Evaluate
    print("Evaluating model...")
    y_pred = model.predict(X_test)
    print("\nClassification Report:")
    print(classification_report(
        y_test, y_pred, 
        target_names=label_encoder.classes_
    ))
    
    # Save model and encoder
    print("Saving model...")
    joblib.dump(model, os.path.join(model_dir, 'mood_model.pkl'))
    joblib.dump(label_encoder, os.path.join(model_dir, 'label_encoder.pkl'))
    print(f"Model saved to {model_dir}")

if __name__ == "__main__":
    train_model()
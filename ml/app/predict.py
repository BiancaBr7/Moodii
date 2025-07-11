import torch
from transformers import AutoTokenizer, AutoModelForSequenceClassification
from pathlib import Path

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

class MoodPredictor:
    """
    Initializes the predictor with the trained model
    Args:
        model_path: Directory containing:
            - model files
            - tokenizer files
            - config.json
    """
    def __init__(self, model_path):
        # Use GPU if available, otherwise CPU
        self.device = "cuda" if torch.cuda.is_available() else "cpu"
        # Loads tokenizer which converts text to numbers
        self.tokenizer = AutoTokenizer.from_pretrained(model_path)
        # Load trained classification model
        self.model = AutoModelForSequenceClassification.from_pretrained(model_path)
        # Move model to appropriate device
        self.model.to(self.device) 
        # Set to eval mode (no training)
        self.model.eval()
    
    """
    Predict mood from input text
    Args: text: Raw input string (e.g., "I'm happy today")
    Returns: Dictionary contraining:
                - emotion: Predicted mood label
                - emoji: Corresponding emoji
                - confidence: 0~1
    """
    def predict(self, text):
        # Tokenize input
        inputs = self.tokenizer(text, return_tensors="pt", truncation=True).to(self.device)
        # Run model inference
        with torch.no_grad():
            outputs = self.model(**inputs)
        
        # Convert raw scores to probabilities
        probs = torch.nn.functional.softmax(outputs.logits, dim=-1)
        #Get predicted class, which is the index with highest probability
        pred_idx = torch.argmax(probs).item()
        # Use index and look up mood name
        emotion = self.model.config.id2label[pred_idx]
        
        return {
            "emotion": emotion,
            "emoji": MOOD_EMOJI_MAP.get(emotion, "❓"),
            "confidence": float(probs[0][pred_idx])
        }
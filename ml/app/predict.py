# import torch
# from transformers import AutoTokenizer, AutoModelForSequenceClassification

# MOOD_EMOJI_MAP = {
#     'happiness': '😊',
#     'sadness': '😢',
#     'anger': '😠',
#     'love': '❤️',
#     'surprise': '😲',
#     'fun': '😄',
#     'hate': '👎',
#     'neutral': '😐',
#     'worry': '😟',
#     'boredom': '🥱',
#     'relief': '😌',
#     'enthusiasm': '🤩',
#     'empty': '◻️'
# }

# class MoodPredictor:
#     def __init__(self, model_path="./models/mood_model"):
#         self.tokenizer = AutoTokenizer.from_pretrained(model_path)
#         self.model = AutoModelForSequenceClassification.from_pretrained(model_path)
#         self.device = torch.device("cuda" if torch.cuda.is_available() else "cpu")
#         self.model.to(self.device)
    
#     def predict(self, text):
#         inputs = self.tokenizer(text, return_tensors="pt", truncation=True, padding=True).to(self.device)
#         with torch.no_grad():
#             logits = self.model(**inputs).logits
#         predicted_class = torch.argmax(logits, dim=1).item()
#         emotion = self.model.config.id2label[predicted_class]
#         emoji = MOOD_EMOJI_MAP[emotion]
#         confidence = torch.softmax(logits, dim=1)[0][predicted_class].item()
#         return {
#             "text": text,
#             "emotion": emotion,
#             "emoji": emoji,
#             "confidence": f"{confidence:.2%}"
#         }

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
    def __init__(self, model_path):
        self.device = "cuda" if torch.cuda.is_available() else "cpu"
        self.tokenizer = AutoTokenizer.from_pretrained(model_path)
        self.model = AutoModelForSequenceClassification.from_pretrained(model_path)
        self.model.to(self.device)
        self.model.eval()
    
    def predict(self, text):
        inputs = self.tokenizer(text, return_tensors="pt", truncation=True).to(self.device)
        with torch.no_grad():
            outputs = self.model(**inputs)
        
        probs = torch.nn.functional.softmax(outputs.logits, dim=-1)
        pred_idx = torch.argmax(probs).item()
        emotion = self.model.config.id2label[pred_idx]
        
        return {
            "emotion": emotion,
            "emoji": MOOD_EMOJI_MAP.get(emotion, "❓"),
            "confidence": float(probs[0][pred_idx])
        }
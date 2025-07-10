import torch
from transformers import AutoTokenizer, AutoModelForSequenceClassification
from training_script import MOOD_EMOJI_MAP

MODEL_PATH = "./mood_model"

class MoodPredictor:
    def __init__(self):
        self.tokenizer = AutoTokenizer.from_pretrained(MODEL_PATH)
        self.model = AutoModelForSequenceClassification.from_pretrained(MODEL_PATH)
        self.device = torch.device("cuda" if torch.cuda.is_available() else "cpu")
        self.model.to(self.device)
    
    def predict(self, text):
        inputs = self.tokenizer(text, return_tensors="pt", truncation=True, padding=True).to(self.device)
        with torch.no_grad():
            logits = self.model(**inputs).logits
        predicted_class = torch.argmax(logits, dim=1).item()
        emotion = self.model.config.id2label[predicted_class]
        emoji = MOOD_EMOJI_MAP[emotion]
        confidence = torch.softmax(logits, dim=1)[0][predicted_class].item()
        return {
            "text": text,
            "emotion": emotion,
            "emoji": emoji,
            "confidence": f"{confidence:.2%}"
        }

def test_manual_input():
    predictor = MoodPredictor()
    print("\nTest the mood predictor (type 'quit' to exit):")
    while True:
        text = input("\nEnter text to analyze: ")
        if text.lower() == 'quit':
            break
        result = predictor.predict(text)
        print(f"\nResult: {result['emoji']} {result['emotion'].upper()} (Confidence: {result['confidence']})")

if __name__ == "__main__":
    test_manual_input()
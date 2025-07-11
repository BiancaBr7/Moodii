import torch
from transformers import AutoTokenizer, AutoModelForSequenceClassification
from training_script import MOOD_EMOJI_MAP

class MoodPredictor:
    def __init__(self, model_path="mood_model"):
        self.device = torch.device("cuda" if torch.cuda.is_available() else "cpu")
        self.tokenizer = AutoTokenizer.from_pretrained(model_path, use_fast=True)
        self.model = AutoModelForSequenceClassification.from_pretrained(model_path).to(self.device)
        self.model.eval()
    
    def predict(self, text):
        inputs = self.tokenizer(text, return_tensors="pt", truncation=True).to(self.device)
        with torch.no_grad():
            outputs = self.model(**inputs)
        
        probs = torch.nn.functional.softmax(outputs.logits, dim=-1)
        pred_idx = torch.argmax(probs).item()
        emotion = self.model.config.id2label[pred_idx]
        
        return {
            "text": text,
            "emotion": emotion,
            "emoji": MOOD_EMOJI_MAP[emotion],
            "confidence": f"{probs[0][pred_idx].item():.2%}",
            "all_probs": {self.model.config.id2label[i]: f"{p:.2%}" 
                         for i, p in enumerate(probs[0].tolist())}
        }

def test_interactive():
    predictor = MoodPredictor()
    print("Mood Prediction Tool (type 'quit' to exit)")
    while True:
        text = input("\nEnter text: ")
        if text.lower() == 'quit':
            break
        result = predictor.predict(text)
        print(f"\nResult: {result['emoji']} {result['emotion']}")
        print(f"Confidence: {result['confidence']}")
        print("All probabilities:")
        for e, p in result['all_probs'].items():
            print(f"- {MOOD_EMOJI_MAP[e]} {e}: {p}")

if __name__ == "__main__":
    test_interactive()
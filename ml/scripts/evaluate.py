import librosa
import numpy as np
import joblib
import os
import sys

# Get the absolute path to the project root directory
PROJECT_ROOT = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))

def load_model():
    """Load the trained model and encoder with proper path handling"""
    try:
        model_path = os.path.join(PROJECT_ROOT, 'models', 'mood_model.pkl')
        encoder_path = os.path.join(PROJECT_ROOT, 'models', 'label_encoder.pkl')
        
        if not os.path.exists(model_path) or not os.path.exists(encoder_path):
            raise FileNotFoundError("Model files not found. Run train_model.py first.")
            
        model = joblib.load(model_path)
        label_encoder = joblib.load(encoder_path)
        return model, label_encoder
    except Exception as e:
        print(f"Error loading model: {str(e)}")
        sys.exit(1)

def predict_mood(audio_path, model, label_encoder):
    """Predict mood from audio file with error handling"""
    try:
        # Load audio with librosa's resilient loader
        audio, sample_rate = librosa.load(audio_path, sr=None, res_type='kaiser_fast')
        
        # Check if audio is valid
        if len(audio) == 0:
            return "Error: Empty audio file"
            
        # Extract features
        mfccs = librosa.feature.mfcc(y=audio, sr=sample_rate, n_mfcc=40)
        mfccs_scaled = np.mean(mfccs.T, axis=0)
        features = mfccs_scaled.reshape(1, -1)
        
        # Predict
        prediction = model.predict(features)
        return label_encoder.inverse_transform(prediction)[0]
    except Exception as e:
        return f"Prediction Error: {str(e)}"

def main():
    model, label_encoder = load_model()
    
    print("\nAudio Mood Prediction System")
    print("="*40)
    print("Enter path to audio file (or 'quit' to exit)\n")
    
    while True:
        audio_path = input("Audio file path: ").strip()
        
        if audio_path.lower() in ['quit', 'exit']:
            break
        
        if not os.path.exists(audio_path):
            print("Error: File not found. Please try again.")
            continue
        
        mood = predict_mood(audio_path, model, label_encoder)
        print(f"\nPredicted Mood: {mood}\n")
        print("="*40)

if __name__ == "__main__":
    main()
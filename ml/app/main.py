from fastapi import FastAPI, File, UploadFile
from pydantic import BaseModel
import numpy as np
import librosa  # For audio analysis
import pickle   # For loading models

app = FastAPI()

# Load pre-trained models
text_model = pickle.load(open("models/text_mood.pkl", "rb"))
audio_model = pickle.load(open("models/audio_mood.pkl", "rb"))

class Prediction(BaseModel):
    mood_id: int
    transcription: str
    confidence: float

@app.post("/predict", response_model=Prediction)
async def predict(audio: UploadFile = File(...)):
    # 1. Process audio
    audio_data, _ = librosa.load(audio.file, sr=22050)
    mfcc = librosa.feature.mfcc(y=audio_data, sr=22050)
    
    # 2. Get audio mood prediction
    audio_pred = audio_model.predict(mfcc.mean(axis=1).reshape(1, -1))
    
    # 3. Get transcription (Mock - replace with Whisper API)
    transcription = "Sample transcription"
    
    # 4. Get text mood prediction
    text_pred = text_model.predict([transcription])
    
    return {
        "mood_id": int(text_pred[0]),
        "transcription": transcription,
        "confidence": float(np.max(audio_pred))
    }
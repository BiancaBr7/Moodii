from fastapi import FastAPI
from pydantic import BaseModel
from predict import MoodPredictor

app = FastAPI()
predictor = MoodPredictor()

class TextRequest(BaseModel):
    text: str

@app.post("/predict")
def predict_mood(request: TextRequest):
    return predictor.predict(request.text)

@app.get("/")
def read_root():
    return {"message": "Mood Prediction API - Send POST request to /predict with text"}
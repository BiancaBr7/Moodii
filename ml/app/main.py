from fastapi import FastAPI
from pydantic import BaseModel
from predict import MoodPredictor
import uvicorn

app = FastAPI()
predictor = MoodPredictor("../models/mood_model")  # Path to trained model

class TextRequest(BaseModel):
    text: str

@app.post("/predict")
async def predict_mood(request: TextRequest):
    return predictor.predict(request.text)

@app.get("/")
async def health_check():
    return {"status": "ready"}

if __name__ == "__main__":
    uvicorn.run(app, host="0.0.0.0", port=8000)
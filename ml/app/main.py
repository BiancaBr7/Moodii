from fastapi import FastAPI
from pydantic import BaseModel
from predict import MoodPredictor
import uvicorn

app = FastAPI()
# Path to trained model
predictor = MoodPredictor("../models/mood_model")  

"""
TextRequest defines the request body structure for the prediction endpoint
"""
class TextRequest(BaseModel):
    text: str #represents an input text to analyze for mood

"""
Endpoint that handles mood and prediction requests

Args: request: JSON containing 'text' field
Returns: Dictionary with emotion, emoji, and confidence
"""
@app.post("/predict")
async def predict_mood(request: TextRequest):
    return predictor.predict(request.text)

"""
Health check endpoint for service monitoring
"""
@app.get("/")
async def health_check():
    return {"status": "ready"}

if __name__ == "__main__":
    # to make requests, go to the port 8000
    uvicorn.run(app, host="0.0.0.0", port=8000)
#!/bin/bash
# Docker API Test Script

echo "Testing Dockerized CNN+LSTM Emotion Recognition API"
echo "======================================================"

API_URL="http://localhost:5000"

# Test 1: Health Check
echo "1. Testing Health Endpoint..."
curl -s "$API_URL/health" | python -m json.tool
echo ""

# Test 2: Model Info
echo "2. Testing Model Info Endpoint..."
curl -s "$API_URL/model-info" | python -m json.tool
echo ""

# Test 3: Test with sample audio (if available)
if [ -f "test_audio.wav" ]; then
    echo "3. Testing Prediction with test_audio.wav..."
    curl -s -X POST -F "audio=@test_audio.wav" "$API_URL/predict" | python -m json.tool
else
    echo "3. No test audio file found (test_audio.wav)"
    echo "   To test prediction, place an audio file named 'test_audio.wav' in this directory"
fi

echo ""
echo "Docker API test completed!"
echo "Check container logs: docker-compose logs emotion-api"
echo "Stop container: docker-compose down"

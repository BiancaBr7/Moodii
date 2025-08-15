#!/bin/bash
# Quick Docker deployment script for CNN+LSTM Emotion Recognition API

echo "Docker Quick Deploy - CNN+LSTM Emotion Recognition API"
echo "========================================================="

# Check if Docker is installed
if ! command -v docker &> /dev/null; then
    echo "Docker is not installed. Please install Docker first."
    echo "Visit: https://docs.docker.com/get-docker/"
    exit 1
fi

# Check if Docker Compose is installed
if ! command -v docker-compose &> /dev/null; then
    echo "Docker Compose is not installed. Please install Docker Compose first."
    exit 1
fi

# Check if model file exists
if [ ! -f "model_CLSTM_20_82.h5" ]; then
    echo "Model file 'model_CLSTM_20_82.h5' not found!"
    echo "Please ensure the model file is in the current directory."
    exit 1
fi

echo "Docker and Docker Compose found!"
echo "Model file found!"

# Create logs directory if it doesn't exist
mkdir -p logs

echo "Building and starting Docker container..."
echo "This may take a few minutes on first run..."

# Build and start with docker-compose
docker-compose up --build -d

# Wait for container to be healthy
echo "Waiting for container to start..."
sleep 10

# Check if container is running
if docker ps | grep -q "moodii-emotion-api"; then
    echo "Container is running!"
    
    # Test health endpoint
    echo "Testing health endpoint..."
    if curl -f http://localhost:5000/health > /dev/null 2>&1; then
        echo "API is healthy and ready!"
        echo ""
        echo "Your API is now available at: http://localhost:5000"
        echo "Available endpoints:"
        echo "   - GET  /health      - Health check"
        echo "   - GET  /model-info  - Model information"
        echo "   - POST /predict     - Emotion prediction"
        echo ""
        echo "View logs with: docker-compose logs -f emotion-api"
        echo "Stop with: docker-compose down"
    else
        echo "Container started but API not responding yet."
        echo "   Check logs with: docker-compose logs emotion-api"
    fi
else
    echo "Container failed to start."
    echo "   Check logs with: docker-compose logs emotion-api"
    exit 1
fi

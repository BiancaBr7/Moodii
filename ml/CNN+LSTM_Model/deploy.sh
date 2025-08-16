#!/bin/bash
# Production deployment script for CNN+LSTM Emotion Recognition API

echo "CNN+LSTM Emotion Recognition API - Production Deployment"
echo "=========================================================="

# Check if virtual environment exists
if [ ! -d "venv" ]; then
    echo "📦 Creating virtual environment..."
    python -m venv venv
fi

# Activate virtual environment
echo "Activating virtual environment..."
source venv/bin/activate  # For Linux/Mac
# For Windows: venv\Scripts\activate

# Install dependencies
echo "Installing dependencies..."
pip install -r requirements.txt

# Check if model exists
if [ ! -f "model_CLSTM_20_82.h5" ]; then
    echo "Error: Model file 'model_CLSTM_20_82.h5' not found!"
    echo "Please ensure the model file is in the current directory."
    exit 1
fi

echo "Model file found!"

# Create production configuration
echo "Setting up production configuration..."

# Set environment variables
export FLASK_ENV=production
export PYTHONPATH="${PYTHONPATH}:$(pwd)"

echo "Starting production server with Gunicorn..."
echo "Server will be available at: http://localhost:5000"
echo "Logs will be written to: emotion_api.log"
echo "=========================================================="

# Start with Gunicorn (production WSGI server)
gunicorn --bind 0.0.0.0:5000 \
         --workers 4 \
         --timeout 120 \
         --max-requests 1000 \
         --max-requests-jitter 100 \
         --access-logfile emotion_api_access.log \
         --error-logfile emotion_api_error.log \
         --log-level info \
         api:app

@echo off
REM Production deployment script for CNN+LSTM Emotion Recognition API (Windows)

echo CNN+LSTM Emotion Recognition API - Production Deployment
echo ==========================================================

REM Check if virtual environment exists
if not exist "venv" (
    echo Creating virtual environment...
    python -m venv venv
)

REM Activate virtual environment
echo Activating virtual environment...
call venv\Scripts\activate

REM Install dependencies
echo Installing dependencies...
pip install -r requirements.txt

REM Check if model exists
if not exist "model_CLSTM_20_82.h5" (
    echo Error: Model file 'model_CLSTM_20_82.h5' not found!
    echo Please ensure the model file is in the current directory.
    exit /b 1
)

echo Model file found!

REM Create production configuration
echo Setting up production configuration...

REM Set environment variables
set FLASK_ENV=production
set PYTHONPATH=%PYTHONPATH%;%CD%

echo Starting production server with Gunicorn...
echo Server will be available at: http://localhost:5000
echo Logs will be written to: emotion_api.log
echo ==========================================================

REM Start with Gunicorn (production WSGI server)
gunicorn --bind 0.0.0.0:5000 ^
         --workers 4 ^
         --timeout 120 ^
         --max-requests 1000 ^
         --max-requests-jitter 100 ^
         --access-logfile emotion_api_access.log ^
         --error-logfile emotion_api_error.log ^
         --log-level info ^
         api:app

pause

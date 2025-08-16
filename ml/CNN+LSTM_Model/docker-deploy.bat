@echo off
REM Quick Docker deployment script for CNN+LSTM Emotion Recognition API (Windows)

echo Docker Quick Deploy - CNN+LSTM Emotion Recognition API
echo =========================================================

REM Check if Docker is installed
docker --version >nul 2>&1
if errorlevel 1 (
    echo Docker is not installed. Please install Docker Desktop first.
    echo Visit: https://docs.docker.com/desktop/windows/
    pause
    exit /b 1
)

REM Check if Docker Compose is installed
docker-compose --version >nul 2>&1
if errorlevel 1 (
    echo Docker Compose is not installed. Please install Docker Desktop which includes Compose.
    pause
    exit /b 1
)

REM Check if model file exists
if not exist "model_CLSTM_20_82.h5" (
    echo Model file 'model_CLSTM_20_82.h5' not found!
    echo Please ensure the model file is in the current directory.
    pause
    exit /b 1
)

echo Docker and Docker Compose found!
echo Model file found!

REM Create logs directory if it doesn't exist
if not exist "logs" mkdir logs

echo Building and starting Docker container...
echo This may take a few minutes on first run...

REM Build and start with docker-compose
docker-compose up --build -d

REM Wait for container to start
echo Waiting for container to start...
timeout /t 10 /nobreak >nul

REM Check if container is running
docker ps | findstr "moodii-emotion-api" >nul
if errorlevel 1 (
    echo    Container failed to start.
    echo    Check logs with: docker-compose logs emotion-api
    pause
    exit /b 1
)

echo Container is running!

REM Test health endpoint
echo Testing health endpoint...
curl -f http://localhost:5000/health >nul 2>&1
if errorlevel 1 (
    echo    Container started but API not responding yet.
    echo    Check logs with: docker-compose logs emotion-api
) else (
    echo API is healthy and ready!
    echo.
    echo Your API is now available at: http://localhost:5000
    echo Available endpoints:
    echo    - GET  /health      - Health check
    echo    - GET  /model-info  - Model information
    echo    - POST /predict     - Emotion prediction
    echo.
    echo View logs with: docker-compose logs -f emotion-api
    echo Stop with: docker-compose down
)

pause

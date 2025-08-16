@echo off
REM Direct Docker deployment on D: drive (no copying needed)

echo Direct Docker Deployment on D: Drive
echo ========================================

REM Check if we're on D: drive
if /i not "%~d0"=="D:" (
    echo This script should be run from D: drive location
    echo Current location: %~dp0
    echo Please navigate to D: drive first
    pause
    exit /b 1
)

echo Running from D: drive: %~dp0

REM Check Docker
docker --version >nul 2>&1
if errorlevel 1 (
    echo Docker not found. Make sure Docker Desktop is running.
    pause
    exit /b 1
)

echo Docker found and running

REM Check model file
if not exist "model_CLSTM_20_82.h5" (
    echo Model file not found!
    pause
    exit /b 1
)

echo Model file found

REM Create logs directory on D: drive
if not exist "logs" mkdir logs

echo Building container directly on D: drive...
docker build -t moodii-emotion-api .

if errorlevel 1 (
    echo Build failed!
    pause
    exit /b 1
)

echo Build successful!

REM Stop any existing container
docker stop moodii-emotion-api 2>nul
docker rm moodii-emotion-api 2>nul

echo Starting container with D: drive volumes...
docker run -d ^
  --name moodii-emotion-api ^
  -p 5000:5000 ^
  -v "%~dp0logs:/app/logs" ^
  -v "%~dp0model_CLSTM_20_82.h5:/app/model_CLSTM_20_82.h5:ro" ^
  moodii-emotion-api

if errorlevel 1 (
    echo Container start failed!
    docker logs moodii-emotion-api
    pause
    exit /b 1
)

echo Container started successfully!
echo API URL: http://localhost:5000
echo Status: docker ps
echo Logs: docker logs moodii-emotion-api
echo Stop: docker stop moodii-emotion-api

REM Test the API
echo Testing API health...
timeout /t 5 /nobreak >nul
curl -s http://localhost:5000/health >nul 2>&1
if not errorlevel 1 (
    echo API is healthy!
) else (
    echo API not responding yet, check logs
)

pause

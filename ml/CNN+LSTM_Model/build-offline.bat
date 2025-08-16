@echo off
echo Offline Docker Build (No Internet Required)
echo ==========================================

REM Check if we have a local Python base image
docker images python:3.9 | findstr "3.9" >nul
if errorlevel 1 (
    echo No local Python 3.9 image found.
    echo Please run: docker pull python:3.9
    echo Or use test-network.bat to diagnose connectivity issues
    pause
    exit /b 1
)

echo Local Python image found, building offline...

REM Use --network=none for offline build
docker build --network=none -t moodii-emotion-api-offline .

if errorlevel 1 (
    echo Offline build failed. This usually means:
    echo 1. Missing local base image (python:3.9)
    echo 2. Dockerfile tries to download packages
    echo.
    echo Try: docker pull python:3.9 (when network works)
    pause
    exit /b 1
)

echo Offline build successful!

REM Start container
docker stop moodii-emotion-api-offline 2>nul
docker rm moodii-emotion-api-offline 2>nul

docker run -d ^
  --name moodii-emotion-api-offline ^
  -p 5001:5000 ^
  -v "%~dp0logs:/app/logs" ^
  -v "%~dp0model_CLSTM_20_82.h5:/app/model_CLSTM_20_82.h5:ro" ^
  moodii-emotion-api-offline

echo Container started on port 5001 (to avoid conflicts)
echo Test: http://localhost:5001/health

pause

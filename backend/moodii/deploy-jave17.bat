@echo off
REM filepath: d:\Moodii\Moodii\backend\moodii\deploy-java17.bat

echo Moodii Backend - Docker Deployment (Java 17)
echo ===============================================

REM Check if Docker is running
docker version >nul 2>&1
if errorlevel 1 (
    echo Docker is not running! Please start Docker Desktop.
    pause
    exit /b 1
)

echo Docker found and running

REM Check if pom.xml exists
if not exist "pom.xml" (
    echo Error: pom.xml not found! Make sure you're in the backend directory.
    pause
    exit /b 1
)

echo Maven project found

REM Create necessary directories
if not exist "logs" mkdir logs
if not exist "uploads" mkdir uploads

echo Building with Java 17...
docker build -f Dockerfile.java17 -t moodii-backend-java17 .

if errorlevel 1 (
    echo Build failed!
    pause
    exit /b 1
)

echo Build successful!

REM Stop any existing container
docker stop moodii-backend 2>nul
docker rm moodii-backend 2>nul

echo Starting container...
docker run -d ^
  --name moodii-backend ^
  -p 8080:8080 ^
  -v "%~dp0logs:/app/logs" ^
  -v "%~dp0uploads:/app/uploads" ^
  moodii-backend-java17

echo Waiting for backend to start...
timeout /t 15 /nobreak >nul

REM Test the API
curl http://localhost:8080/actuator/health 2>nul
if errorlevel 1 (
    echo Backend might still be starting...
    echo Check logs: docker logs moodii-backend
) else (
    echo Backend started successfully!
)

echo.
echo Backend API: http://localhost:8080
echo Health check: http://localhost:8080/actuator/health
echo.
echo Management commands:
echo   View logs: docker logs -f moodii-backend
echo   Stop: docker stop moodii-backend
echo   Container shell: docker exec -it moodii-backend bash
echo.

pause
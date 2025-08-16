@echo off
REM filepath: d:\Moodii\Moodii\backend\deploy-backend.bat

echo Moodii Backend - Docker Deployment
echo ===================================

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

echo Building and starting backend container...
docker-compose down
docker-compose up --build -d

if errorlevel 1 (
    echo Build failed!
    pause
    exit /b 1
)

echo Build successful!

echo Waiting for backend to start...
timeout /t 10 /nobreak >nul

REM Check if container is running
docker ps | findstr moodii-backend >nul
if errorlevel 1 (
    echo Container failed to start!
    echo Check logs: docker-compose logs moodii-backend
    pause
    exit /b 1
)

echo Backend started successfully!
echo.
echo Backend API: http://localhost:8080
echo Health check: http://localhost:8080/actuator/health
echo.
echo Management commands:
echo   View logs: docker-compose logs -f moodii-backend
echo   Stop: docker-compose down
echo   Restart: docker-compose restart
echo.

pause
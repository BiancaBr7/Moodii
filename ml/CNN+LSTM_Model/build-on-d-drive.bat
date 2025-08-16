@echo off
REM Move Docker project to D: drive and build there

echo Building Docker container on D: drive to save C: drive space
echo ================================================================

REM Create D: drive Docker directory
if not exist "D:\DockerProjects" mkdir D:\DockerProjects
if not exist "D:\DockerProjects\MoodiiAPI" mkdir D:\DockerProjects\MoodiiAPI

echo Copying project files to D: drive...
xcopy "%~dp0*" "D:\DockerProjects\MoodiiAPI\" /S /E /Y

echo Changing to D: drive location...
cd /d D:\DockerProjects\MoodiiAPI

echo Building Docker container on D: drive...
docker build -t moodii-emotion-api .

echo Starting container...
docker run -d --name moodii-emotion-api -p 5000:5000 -v D:\DockerProjects\MoodiiAPI\logs:/app/logs moodii-emotion-api

echo Container built and started from D: drive!
echo API available at: http://localhost:5000
echo Check status: docker ps
echo Stop with: docker stop moodii-emotion-api

pause

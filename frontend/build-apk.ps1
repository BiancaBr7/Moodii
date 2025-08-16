# Android APK Build and Extract Script (PowerShell)

Write-Host "🚀 Building Android APK using Docker..." -ForegroundColor Green

# Create output directory
if (!(Test-Path "apk-output")) {
    New-Item -ItemType Directory -Path "apk-output" | Out-Null
}

# Clean previous APKs
Write-Host "🧹 Cleaning previous APKs..." -ForegroundColor Blue
Remove-Item -Path "apk-output\*.apk" -Force -ErrorAction SilentlyContinue

# Build the Docker image
Write-Host "📦 Building Docker image..." -ForegroundColor Blue
docker-compose build

if ($LASTEXITCODE -ne 0) {
    Write-Host "❌ Docker build failed!" -ForegroundColor Red
    exit 1
}

# Copy APK to host using a temporary container
Write-Host "📱 Copying APK to host..." -ForegroundColor Yellow
$containerId = docker run -d frontend-frontend-extract:latest sleep 30
docker cp ${containerId}:/output/. ./apk-output/
docker rm -f $containerId

# Check if APK was extracted successfully
$apkFiles = Get-ChildItem "./apk-output/*.apk" -ErrorAction SilentlyContinue
if ($apkFiles) {
    Write-Host "✅ APK build successful!" -ForegroundColor Green
    foreach ($apk in $apkFiles) {
        $size = [math]::Round($apk.Length / 1MB, 1)
        Write-Host "📦 Generated: $($apk.Name) ($size MB)" -ForegroundColor Cyan
    }
    Write-Host ""
    Write-Host "🎉 Your APK is ready in the apk-output/ directory" -ForegroundColor Green
    Write-Host "📱 Install on Android device: adb install apk-output\app-debug.apk" -ForegroundColor Cyan
} else {
    Write-Host "❌ No APK files found. Check the build logs above." -ForegroundColor Red
    exit 1
}

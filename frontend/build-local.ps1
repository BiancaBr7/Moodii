# Local Android Build Script (No Docker)
# This builds the APK directly on your local machine

Write-Host "🚀 Building Android APK locally..." -ForegroundColor Green

# Check if Android SDK is available
if (-not $env:ANDROID_HOME) {
    Write-Host "❌ ANDROID_HOME environment variable not set." -ForegroundColor Red
    Write-Host "Please install Android Studio and set ANDROID_HOME." -ForegroundColor Yellow
    exit 1
}

# Check if Java is available
try {
    $javaVersion = java -version 2>&1
    Write-Host "☕ Java version: $($javaVersion[0])" -ForegroundColor Cyan
} catch {
    Write-Host "❌ Java not found. Please install JDK 17 or later." -ForegroundColor Red
    exit 1
}

# Create output directory
if (!(Test-Path "apk-output")) {
    New-Item -ItemType Directory -Path "apk-output" | Out-Null
}

# Clean previous build
Write-Host "🧹 Cleaning previous build..." -ForegroundColor Blue
& .\gradlew.bat clean

# Build debug APK
Write-Host "🔨 Building debug APK..." -ForegroundColor Blue
& .\gradlew.bat assembleDebug

if ($LASTEXITCODE -eq 0) {
    # Copy APK to output directory
    $apkPath = "app\build\outputs\apk\debug\*.apk"
    if (Test-Path $apkPath) {
        Copy-Item $apkPath -Destination "apk-output\" -Force
        Write-Host "✅ Build successful!" -ForegroundColor Green
        Write-Host "📱 APK location:" -ForegroundColor Cyan
        Get-ChildItem -Path "apk-output" | Format-Table Name, Length, LastWriteTime
    } else {
        Write-Host "❌ APK file not found at expected location." -ForegroundColor Red
    }
} else {
    Write-Host "❌ Build failed!" -ForegroundColor Red
    exit 1
}

# Moodii Deployment Script using GHCR (PowerShell)
# This script pulls the latest images from GitHub Container Registry and deploys them

param(
    [string]$Tag = "latest",
    [string]$Registry = "ghcr.io",
    [string]$Repo = "biancabr7/moodii"
)

Write-Host "🚀 Starting Moodii deployment..." -ForegroundColor Green

Write-Host "📦 Registry: $Registry" -ForegroundColor Cyan
Write-Host "📦 Repository: $Repo" -ForegroundColor Cyan
Write-Host "🏷️  Tag: $Tag" -ForegroundColor Cyan

# Login to GHCR (requires GITHUB_TOKEN environment variable)
if ($env:GITHUB_TOKEN) {
    Write-Host "🔐 Logging in to GitHub Container Registry..." -ForegroundColor Yellow
    $env:GITHUB_TOKEN | docker login $Registry -u $env:GITHUB_USERNAME --password-stdin
} else {
    Write-Host "⚠️  GITHUB_TOKEN not set. You may need to login manually: docker login ghcr.io" -ForegroundColor Yellow
}

# Pull latest images
Write-Host "⬇️  Pulling latest images..." -ForegroundColor Blue
docker pull "$Registry/$Repo/backend:$Tag"
docker pull "$Registry/$Repo/ml-api:$Tag"
docker pull "$Registry/$Repo/frontend:$Tag"

# Stop existing containers
Write-Host "🛑 Stopping existing containers..." -ForegroundColor Red
docker-compose -f docker-compose.prod.yml down

# Start services
Write-Host "🔄 Starting services..." -ForegroundColor Green
docker-compose -f docker-compose.prod.yml up -d

# Wait for services to be healthy
Write-Host "⏳ Waiting for services to start..." -ForegroundColor Yellow
Start-Sleep -Seconds 30

# Check service health
Write-Host "🏥 Checking service health..." -ForegroundColor Magenta
try {
    $response = Invoke-WebRequest -Uri "http://localhost:8080/actuator/health" -TimeoutSec 10
    if ($response.StatusCode -eq 200) {
        Write-Host "✅ Backend is healthy" -ForegroundColor Green
    }
} catch {
    Write-Host "❌ Backend health check failed" -ForegroundColor Red
}

try {
    $response = Invoke-WebRequest -Uri "http://localhost:5000/health" -TimeoutSec 10
    if ($response.StatusCode -eq 200) {
        Write-Host "✅ ML API is healthy" -ForegroundColor Green
    }
} catch {
    Write-Host "❌ ML API health check failed" -ForegroundColor Red
}

Write-Host "🎉 Deployment complete!" -ForegroundColor Green
Write-Host "🌐 Backend: http://localhost:8080" -ForegroundColor Cyan
Write-Host "🤖 ML API: http://localhost:5000" -ForegroundColor Cyan
Write-Host "📱 APK output: ./frontend/app/build/outputs/apk/" -ForegroundColor Cyan

# Show running containers
Write-Host "📊 Running containers:" -ForegroundColor Magenta
docker-compose -f docker-compose.prod.yml ps

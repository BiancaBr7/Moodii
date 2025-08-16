#!/bin/bash

# Moodii Deployment Script using GHCR
# This script pulls the latest images from GitHub Container Registry and deploys them

set -e

echo "🚀 Starting Moodii deployment..."

# Configuration
REGISTRY="ghcr.io"
REPO="biancabr7/moodii"
TAG="${1:-latest}"

echo "📦 Registry: $REGISTRY"
echo "📦 Repository: $REPO"
echo "🏷️  Tag: $TAG"

# Login to GHCR (requires GITHUB_TOKEN environment variable)
if [ -n "$GITHUB_TOKEN" ]; then
    echo "🔐 Logging in to GitHub Container Registry..."
    echo $GITHUB_TOKEN | docker login $REGISTRY -u $GITHUB_USERNAME --password-stdin
else
    echo "⚠️  GITHUB_TOKEN not set. You may need to login manually: docker login ghcr.io"
fi

# Pull latest images
echo "⬇️  Pulling latest images..."
docker pull $REGISTRY/$REPO/backend:$TAG
docker pull $REGISTRY/$REPO/ml-api:$TAG
docker pull $REGISTRY/$REPO/frontend:$TAG

# Stop existing containers
echo "🛑 Stopping existing containers..."
docker-compose -f docker-compose.prod.yml down

# Start services
echo "🔄 Starting services..."
docker-compose -f docker-compose.prod.yml up -d

# Wait for services to be healthy
echo "⏳ Waiting for services to start..."
sleep 30

# Check service health
echo "🏥 Checking service health..."
if curl -f http://localhost:8080/actuator/health > /dev/null 2>&1; then
    echo "✅ Backend is healthy"
else
    echo "❌ Backend health check failed"
fi

if curl -f http://localhost:5000/health > /dev/null 2>&1; then
    echo "✅ ML API is healthy"
else
    echo "❌ ML API health check failed"
fi

echo "🎉 Deployment complete!"
echo "🌐 Backend: http://localhost:8080"
echo "🤖 ML API: http://localhost:5000"
echo "📱 APK output: ./frontend/app/build/outputs/apk/"

# Show running containers
echo "📊 Running containers:"
docker-compose -f docker-compose.prod.yml ps

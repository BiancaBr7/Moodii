#!/bin/bash

# Android APK Build and Extract Script

echo "Building Android APK using Docker..."

# Create output directory
mkdir -p apk-output

# Build the Docker image
echo "Building Docker image..."
docker-compose build

# Run container to extract APK
echo "Extracting APK files..."
docker-compose run --rm frontend

# Check if APK was created
if [ "$(ls -A apk-output)" ]; then
    echo "Build successful! APK files:"
    ls -la apk-output/
    echo ""
    echo "Your APK is ready in the apk-output/ directory"
else
    echo "No APK files found. Check the build logs above."
    exit 1
fi

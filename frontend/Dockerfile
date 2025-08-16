# Multi-stage Android build
FROM openjdk:17-jdk-slim as builder

# Set environment variables
ENV ANDROID_HOME=/opt/android-sdk
ENV PATH=$PATH:$ANDROID_HOME/cmdline-tools/latest/bin:$ANDROID_HOME/platform-tools
ENV DEBIAN_FRONTEND=noninteractive

# Install dependencies
RUN apt-get update && apt-get install -y \
    wget \
    unzip \
    curl \
    ca-certificates \
    && rm -rf /var/lib/apt/lists/*

# Install Android SDK
RUN mkdir -p $ANDROID_HOME/cmdline-tools && \
    cd $ANDROID_HOME/cmdline-tools && \
    wget -q https://dl.google.com/android/repository/commandlinetools-linux-9477386_latest.zip && \
    unzip -q commandlinetools-linux-9477386_latest.zip && \
    mv cmdline-tools latest && \
    rm commandlinetools-linux-9477386_latest.zip

# Accept licenses and install SDK components
RUN yes | $ANDROID_HOME/cmdline-tools/latest/bin/sdkmanager --licenses && \
    $ANDROID_HOME/cmdline-tools/latest/bin/sdkmanager \
        "platform-tools" \
        "platforms;android-34" \
        "build-tools;34.0.0"

# Set working directory
WORKDIR /app

# Copy project files (excluding problematic files)
COPY gradle.properties ./
COPY build.gradle.kts ./
COPY settings.gradle.kts ./
COPY gradle/ ./gradle/
COPY gradlew ./
COPY app/ ./app/

# Make gradlew executable
RUN chmod +x gradlew

# Build the APK
RUN ./gradlew assembleDebug --no-daemon --stacktrace

# Final stage - extract APK only
FROM scratch as apk-extractor

# Copy APK from builder
COPY --from=builder /app/app/build/outputs/apk/debug/*.apk /

# Runtime stage for running container
FROM alpine:latest

# Install file utilities
RUN apk add --no-cache file

# Copy APK from builder
COPY --from=builder /app/app/build/outputs/apk/debug/*.apk /output/

# Show APK info
CMD ["sh", "-c", "ls -la /output/ && file /output/*.apk && echo 'Android APK ready for installation!'"]

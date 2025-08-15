# Use a pre-built Android development image
FROM cimg/android:2024.01.1

# Set working directory
WORKDIR /app

# Set environment variables for Gradle
ENV GRADLE_OPTS="-Dorg.gradle.daemon=false -Dorg.gradle.parallel=false"

# Copy only necessary files first for better caching
COPY gradle.properties ./
COPY gradlew ./
COPY gradle/ ./gradle/
COPY build.gradle.kts ./
COPY settings.gradle.kts ./

# Make gradlew executable and download dependencies
RUN chmod +x gradlew && ./gradlew dependencies --no-daemon || true

# Copy the rest of the project
COPY . .

# Build the Android project (simple approach)
RUN ./gradlew assembleDebug --no-daemon --stacktrace

# This is a build container - the APK will be in app/build/outputs/apk/
CMD ["echo", "Android build complete. APK available in app/build/outputs/apk/debug/"]

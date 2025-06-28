# Use an official JDK runtime as a parent image
FROM eclipse-temurin:17-jdk AS build

# Set working directory
WORKDIR /app

# Copy everything (including pom.xml and mvnw)
COPY . .

# Build the application using Maven wrapper
RUN ./mvnw clean package -DskipTests

# ====== Final image ======
FROM eclipse-temurin:17-jdk

# Set working directory in the container
WORKDIR /app

# Copy the jar from the builder image
COPY --from=build /app/target/moodii-0.0.1-SNAPSHOT.jar app.jar

# Expose port
EXPOSE 8080

# Run the jar
ENTRYPOINT ["java", "-jar", "app.jar"]

# Use Eclipse Temurin as the base image
FROM eclipse-temurin:17-jdk-alpine

# Set the working directory in the container
WORKDIR /app

# Copy the build artifacts from the host to the container
COPY build/libs/demo-0.0.1-SNAPSHOT.jar user-service.jar

# Expose the port the application runs on
EXPOSE 8081

# Define the command to run the application
CMD ["java", "-jar", "user-service.jar"]



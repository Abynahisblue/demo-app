FROM openjdk:17-jdk-slim

# Create app directory
WORKDIR /app

# Copy the jar file
COPY target/java-ecr-demo.jar app.jar

# Expose port
EXPOSE 8080

# Add health check
HEALTHCHECK --interval=30s --timeout=10s --start-period=60s --retries=3 \
  CMD curl -f http://localhost:8080/health || exit 1

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]
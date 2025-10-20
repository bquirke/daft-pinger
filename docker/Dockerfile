# Multi-stage Docker build for Daft Pinger
FROM gradle:8.14.3-jdk17 AS builder

# Set working directory
WORKDIR /app

# Copy gradle wrapper files first for dependency caching
COPY gradlew gradlew.bat ./
COPY gradle/ gradle/
COPY build.gradle settings.gradle ./

# Make gradlew executable
RUN chmod +x gradlew

# Copy source code
COPY src/ src/

# Build the application
RUN gradle bootJar --no-daemon

# Runtime stage with minimal JRE and Chrome
FROM eclipse-temurin:17-jre-jammy

# Install Chrome and dependencies for headless browsing
RUN apt-get update && apt-get install -y \
    wget \
    gnupg \
    curl \
    && wget -q -O - https://dl.google.com/linux/linux_signing_key.pub | gpg --dearmor -o /usr/share/keyrings/google-archive-keyring.gpg \
    && echo "deb [arch=amd64 signed-by=/usr/share/keyrings/google-archive-keyring.gpg] http://dl.google.com/linux/chrome/deb/ stable main" > /etc/apt/sources.list.d/google-chrome.list \
    && apt-get update \
    && apt-get install -y google-chrome-stable \
    && rm -rf /var/lib/apt/lists/* \
    && apt-get clean

# Create non-root user for security
RUN groupadd -r appuser && useradd -r -g appuser appuser

# Set working directory
WORKDIR /app

# Copy JAR from builder stage
COPY --from=builder /app/build/libs/daft-pinger-*.jar app.jar

# Copy initial data files for seeding the container
COPY --chown=appuser:appuser data/seen-urls.txt /app/data/seen-urls.txt

# Create data directory and selenium cache directory with proper permissions
RUN mkdir -p /app/data /home/appuser/.cache/selenium /tmp && \
    chown -R appuser:appuser /app /home/appuser /tmp && \
    chmod -R 755 /home/appuser/.cache && \
    chmod -R 755 /app/data

# Switch to non-root user
USER appuser

# Expose port (optional, for health checks)
EXPOSE 8080

# Health check
HEALTHCHECK --interval=30s --timeout=10s --start-period=30s --retries=3 \
    CMD curl -f http://localhost:8080/actuator/health || exit 1

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]
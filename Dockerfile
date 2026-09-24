# Multi-stage Dockerfile for Enterprise Garment Manufacturing WMS
# Stage 1: Maven Build
FROM maven:3.9.6-eclipse-temurin-21-alpine AS builder

WORKDIR /build

# Copy POM and download dependencies for caching
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copy source code and package application
COPY src ./src
RUN mvn clean package -DskipTests

# Stage 2: Minimal Distroless / Alpine JRE Runtime
FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

# Add unprivileged user for secure execution
RUN addgroup -S wmsgroup && adduser -S wmsuser -G wmsgroup

# Copy jar from builder stage
COPY --from=builder /build/target/garment-wms-1.0.0.jar app.jar

# Adjust permissions
RUN chown -R wmsuser:wmsgroup /app
USER wmsuser

# Default port
ENV PORT=8080
EXPOSE ${PORT}

# Optimized JVM memory and GC flags for container environments
ENV JAVA_OPTS="-XX:+UseG1GC -XX:MaxRAMPercentage=75.0 -Djava.security.egd=file:/dev/./urandom"

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar /app/app.jar --server.port=${PORT}"]

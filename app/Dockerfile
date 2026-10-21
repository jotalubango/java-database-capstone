# Explanation
#     FROM maven: First stage uses Maven and JDK 17 to compile the application.
#     COPY: Brings in your Maven config and source code.
#     RUN mvn clean package -DskipTests: Builds the .jar file.
#     FROM eclipse-temurin: Second stage uses a smaller base image with only the JRE.
#     COPY --from=builder: Transfers the built jar from the first stage.
#     EXPOSE 8080: Documents the port your app runs on.
#     ENTRYPOINT: Specifies how the container should run your app.

# Step 1: Use Maven with JDK 17 to build the app
FROM maven:3.9.9-eclipse-temurin-17 AS builder

WORKDIR /app

COPY pom.xml .
COPY src ./src

RUN mvn clean package -DskipTests

# Step 2: Use lightweight JRE 17 for running the app
FROM eclipse-temurin:17.0.15_6-jre AS runtime

# Create non-root user
RUN addgroup -g 1001 -S appuser && \
    adduser -S appuser -G appuser -u 1001

WORKDIR /app

# Copy built artifact from builder stage
COPY --from=builder /app/target/back-end-0.0.1-SNAPSHOT.jar app.jar

# Set ownership to non-root user
RUN chown -R appuser:appuser /app

# Switch to non-root user
USER appuser

# Configure JVM for container environment
ENV JAVA_OPTS="-XX:+UseContainerSupport \
    -XX:MaxRAMPercentage=75.0 \
    -XX:+UseG1GC \
    -Djava.security.egd=file:/dev/./urandom"

EXPOSE 8080

ENTRYPOINT ["java", "$JAVA_OPTS", "-jar", "app.jar"]


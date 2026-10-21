# Dockerfile
#
# Step 1: Use Maven with JDK 17 to compile/build the application
FROM maven:3.9.9-eclipse-temurin-17 AS builder

WORKDIR /app

# COPY: Brings in Maven config and source code.
COPY pom.xml .
COPY src ./src

#     RUN mvn clean package -DskipTests: Builds the .jar file.
RUN mvn clean package -DskipTests

# Step 2: Use lightweight JRE 17 for running the app
FROM eclipse-temurin:17.0.15_6-jre AS runtime

# Create non-root user
RUN groupadd -g 1001 -r appuser && \
    useradd -u 1001 -r -g appuser appuser

WORKDIR /app

# COPY --from=builder: Transfers the built jar from the first stage.
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

# EXPOSE 8080: Documents the port the app runs on.
EXPOSE 8080

# ENTRYPOINT: Specifies how the container should run your app.
ENTRYPOINT ["sh", "-c", "exec java $JAVA_OPTS -jar app.jar"]


# Build
FROM maven:3.9.6-eclipse-temurin-21 AS builder

WORKDIR /app

# Copy dependency manifests first for better layer caching
COPY pom.xml .
RUN mvn dependency:go-offline -q

# Copy source and build
COPY src ./src
RUN mvn clean package -DskipTests -q

# Runtime
FROM eclipse-temurin:21-jre-jammy

# Create a non-root user for security
RUN addgroup --system tradingmk && adduser --system --ingroup tradingmk tradingmk

WORKDIR /app

COPY --from=builder /app/target/*.jar app.jar

RUN chown tradingmk:tradingmk app.jar
USER tradingmk

EXPOSE 8080

#  exec form so signals are handled correctly
ENTRYPOINT ["java", "-XX:+UseContainerSupport", "-XX:MaxRAMPercentage=75.0", "-jar", "app.jar"]

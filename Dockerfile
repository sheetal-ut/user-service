# Build stage
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app
COPY . .
RUN mvn clean package -DskipTests

# Final stage
FROM eclipse-temurin:17-jdk
WORKDIR /app

# Copy the JAR from the build stage
COPY --from=build /app/target/user-service-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 8080

# Entry point with agent
ENTRYPOINT ["java", "-jar", "app.jar"]


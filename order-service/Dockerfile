# Stage 1: Build the application using the Maven image with Eclipse Temurin JDK 22
FROM maven:3.9.9-eclipse-temurin-22-jammy AS build

# Set the working directory
WORKDIR /app

# Copy the Maven project files to the container
COPY pom.xml .
COPY src ./src

# Run Spotless to apply code formatting
RUN mvn spotless:apply

# Run Maven to build the project and skip tests
RUN mvn clean install -DskipTests

# Stage 2: Create the runtime image
FROM eclipse-temurin:22-jdk-jammy

# Set the working directory
WORKDIR /app

# Copy the built JAR file from the build stage
COPY --from=build /app/target/order-service-0.0.1-SNAPSHOT.jar order-service-0.0.1-SNAPSHOT.jar

# Expose the application port
EXPOSE 8081

# Specify the command to run the application
ENTRYPOINT ["java", "-jar", "order-service-0.0.1-SNAPSHOT.jar"]

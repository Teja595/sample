 # Use an official OpenJDK runtime as a base image
FROM openjdk:17-jdk-alpine

ARG JAR_FILE=target/*.jar
# Copy the packaged JAR file into the container
COPY ./target/karthik.jar app.jar

# Specify the command to run your application
ENTRYPOINT ["java", "-jar", "/app.jar"]

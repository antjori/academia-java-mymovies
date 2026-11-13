FROM eclipse-temurin:21-jre
WORKDIR /app

# Copies the JAR file into the container
COPY target/my-movies.jar app.jar

# Exposes the port to access in the container
EXPOSE 8080

# Good practice - non-root user
USER 10001

# Entry point for JVM initialization
ENTRYPOINT ["java", "-jar", "/app/app.jar"]

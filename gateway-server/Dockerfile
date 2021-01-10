FROM fabric8/java-alpine-openjdk11-jdk

LABEL maintainer="Sparky Studios <development@traklibrary.com>"

WORKDIR app

# Copy the jar file into the docker container.
ARG jar=target/traklibrary-gateway-api.jar
COPY ${jar} traklibrary-gateway-api.jar

# Expose this port for external communication.
EXPOSE 8080

ENTRYPOINT ["java", "-Djava.security.egd-file=file:/dev/./urandom", "-jar", "traklibrary-gateway-api.jar"]


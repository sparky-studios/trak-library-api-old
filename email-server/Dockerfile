FROM fabric8/java-alpine-openjdk11-jdk

LABEL maintainer="Sparky Studios <development@traklibrary.com>"

WORKDIR app

# Copy the jar file into the docker container.
ARG jar=target/*.jar
COPY ${jar} email-server.jar

# Additional debug arguments provided by the maven build.
ARG DEBUG_ARGS
ENV ENV_DEBUG=${DEBUG_ARGS}

# Expose this port for external communication.
EXPOSE 8080
# Exposed for debug purposes.
EXPOSE 5005

ENTRYPOINT java ${ENV_DEBUG} -Djava.security.egd-file=file:/dev/./urandom -Xmx256m -jar email-server.jar

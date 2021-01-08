FROM fabric8/java-alpine-openjdk11-jdk

LABEL maintainer="Sparky Studios <development@traklibrary.com>"

WORKDIR app

# Copy the jar file into the docker container.
ARG jar=target/traklibrary-game-server.jar
COPY ${jar} game-server.jar

# Additional debug arguments provided by the maven build.
ARG DEBUG_ARGS
ENV ENV_DEBUG=${DEBUG_ARGS}

ENTRYPOINT ["java", "-Djava.security.egd-file=file:/dev/./urandom", "-Xmx256m", "-jar", "game-server.jar"]
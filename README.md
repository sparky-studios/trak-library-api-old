# Trak Library API

<!-- Project shields -->
[![Build Status][codebuild-badge]][codebuild-url]
[![Quality Gate Status][sonarcloud-quality-gate-badge]][sonarcloud-url]
[![Coverage][sonarcloud-code-coverage-badge]][sonarcloud-url]
[![License][license-badge]][license-url]
[![LinkedIn][linkedin-badge]][linkedin-url]

## Getting started

### Prerequisites

Make sure you have installed all the following prerequisites on your development machine:

- Maven - [Download & Install Maven](https://maven.apache.org/download.cgi) and ensure it's accessible via an environment variable.
- Java 11 - [Download & Install Java 11](https://adoptopenjdk.net/?variant=openjdk11&jvmVariant=hotspot) and ensure it's accessible via an environment variable.
- Docker - [Download & Install Docker](https://www.docker.com/products/docker-desktop) and ensure it's accessible via an environment variable.

### Development setup

#### Setting up a project
Create an **.env** file at the root of the project and add the following values:

```.env
# Encryption key used to encrypt/decrypt config server values.
CONFIG_SERVER_ENCRYPT_KEY=****

# Credentials to access the private git repository storing configuration files.
CONFIG_SERVER_GIT_USERNAME=****
CONFIG_SERVER_GIT_PASSWORD=****

# Basic authentication credentials each service needs to access configuration files.
CONFIG_SERVER_SECURITY_USERNAME=****
CONFIG_SERVER_SECURITY_PASSWORD=****

# Basic authentication credentials each service needs to register itself with the discovery server.
DISCOVERY_SERVER_USERNAME=****
DISCOVERY_SERVER_PASSWORD=****
```

- The encryption key used to decrypt the values stored within configuration repository. This key is only provided on request by an Administrator and can only be used for development configuration values.
- The git credentials will need to be your GitHub username, and the personal access token which allows you to access the configuration repository respectively.
- The basic authentication credentials that services will need to access the configuration files from the configuration server. They should match the values provided to you by an Administrator.
- The basic authentication credentials services will need to register with the discovery server. For the purposes of development these can be set to any arbitrary values.

#### Running the application

To run the application, you'll need to ensure the docker images have been built. This is done via the fabric8io's [Docker Maven Plugin](https://github.com/fabric8io/docker-maven-plugin), with the images being built in the **package** stage.
```shell script
mvn clean package
``` 

Once the images have been built, the application can be deployed by running:
```shell script
docker-compose -f ./docker-compose.yml -f docker-compose.development.yml up -d
```

It is not uncommon to see exceptions when starting the application, this is a work-in-progress to address this.

<!-- Badges -->
[codebuild-badge]: https://codebuild.eu-west-2.amazonaws.com/badges?uuid=eyJlbmNyeXB0ZWREYXRhIjoiZ3pQU2srenU4Mm5Fb2E4MGM4cnFyWDErQW1iMFBhc2dNakdaaG1oVTllRndwbERXZlg5QmJ2UjJaVlVKUElqZzNBd3JWZTA0L2RuS2k1cHZQN3pKdkxvPSIsIml2UGFyYW1ldGVyU3BlYyI6IklEMk5pOEdZT2JqVzBoSmgiLCJtYXRlcmlhbFNldFNlcmlhbCI6MX0%3D&branch=develop
[codebuild-url]: https://eu-west-2.console.aws.amazon.com/codesuite/codebuild/885890504135/projects/trak-api-development
[sonarcloud-quality-gate-badge]: https://sonarcloud.io/api/project_badges/measure?project=sparky-studios_trak-api&metric=alert_status
[sonarcloud-code-coverage-badge]: https://sonarcloud.io/api/project_badges/measure?project=sparky-studios_trak-api&metric=coverage
[sonarcloud-url]: https://sonarcloud.io/dashboard?id=sparky-studios_trak-api
[license-badge]: https://img.shields.io/badge/License-Apache%202.0-blue.svg
[license-url]: https://opensource.org/licenses/Apache-2.0
[linkedin-badge]: https://img.shields.io/badge/-LinkedIn-black.svg?style=flat-square&logo=linkedin&colorB=555
[linkedin-url]: https://linkedin.com/in/benjamin-carter-04a8a3114
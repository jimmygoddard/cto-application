# File Service

REST service which supports storing and retrieving files and their metatdata. The service is written
in Kotlin and depends on a PostgreSQL database.

## Running the File Service

Note: all commands assume you are in the repo's top level directory

1. Build it: `./gradlew build`
1. Through Docker
    1. Build docker container: `./gradlew jibDockerBuild`
    1. Run
       it: `docker-compose -f docker/postgres-container.yml -f docker/file-service-container.yml up`

1. Through Gradle
    1. Run Postgres: `docker-compose -f docker/postgres-container.yml up`
    1. Run the service: `./gradlew bootRun`

1. Through IntelliJ
    1. Run postgres container by going to the [Docker postgres file](docker/postgres-container.yml)
       and hitting the Run button
    1. Run File Service by going to
       the [Main class](src/main/kotlin/com/cogitocorp/service/file/FileServiceApplication.kt) and
       hitting the Run button
       
## Reference

- [OpenAPI v3 Spec](docs/openapi-spec.yml)
- [List of helpful auto-generated links](HELP.md)

# Repository Guidelines

## Project Structure & Module Organization

This repository is a Spring Boot 3.5 / Java 21 service for creating and managing Auth0 users. Main application code lives under `src/main/java/com/oidccall/createUserInAuth0/`, organized by responsibility: `controllers`, `implementation`, `repository`, `entities`, `dtos`, `config`, `filters`, `exceptions`, and `validation`.

Configuration files are in `src/main/resources/`: `application.yml` plus profile-specific files such as `application-dev.yml`, `application-test.yml`, and `application-docker.yml`. Database migrations are in `src/main/resources/db/migration/common/`. Tests and fixtures are under `src/test/java/` and `src/test/resources/`. Docker configuration is in `Dockerfile`, `docker-compose.yml`, and `for-dco-docker.env`.

## Build, Test, and Development Commands

Run the app locally with the dev profile:

```bash
./mvnw spring-boot:run -Dspring-boot.run.profiles=dev
```
/asdf
Run local unit tests, assuming PostgreSQL is already available:

```bash
./mvnw clean test -Dspring.profiles.active=dev
```

Run tests inside Docker, including the PostgreSQL dependency:

```bash
docker compose --env-file for-dco-docker.env run --rm --build test
```

Build and start the application container:

```bash
docker compose --env-file for-dco-docker.env up -d --build createuser
```

Run integration tests:

```bash
test-integration/test-em-all1.bash
```

## Coding Style & Naming Conventions

Use standard Java conventions: four-space indentation, `PascalCase` classes, `camelCase` methods and fields, and package names in lowercase. Keep Spring components in the existing package structure and prefer explicit names such as `UserImplementation`, `UsersRepository`, or `PhoneNumberValidator`. Lombok is available, but avoid adding it where simple Java is clearer.

## Testing Guidelines

Tests use Spring Boot Test, JUnit, Mockito, H2, and PostgreSQL depending on profile. Unit tests should end with `Test`; integration-style tests currently use names ending in `IT`. Place fixtures near the feature they support in `src/test/resources/`, following existing folders like `UserImplementation/createUserInAuth0/`.

## Commit & Pull Request Guidelines

Recent commits use the prefix `Update-app:` followed by a short imperative summary. Keep commits focused and mention schema, Docker, or profile changes explicitly when relevant. Pull requests should include a concise description, test commands run, linked issue if any, and notes for configuration or migration changes.

## Security & Configuration Tips

Do not hardcode Auth0, Okta, database, or functional API secrets in Java or YAML. Prefer environment files for local development and runtime environment variables or secrets in deployed environments. Choose `SPRING_PROFILES_ACTIVE` at container launch time rather than baking environment-specific behavior into the image.

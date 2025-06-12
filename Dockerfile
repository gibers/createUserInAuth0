# Stage de test
FROM eclipse-temurin:21-jdk-jammy AS test
WORKDIR /app
COPY .mvn/ ./.mvn/
COPY mvnw pom.xml .env ./
COPY src ./src
RUN chmod +x mvnw
RUN ./mvnw clean test

FROM eclipse-temurin:21-jre-jammy AS builder
WORKDIR /extracted
ADD ./target/createUserInAuth0-0.0.1-SNAPSHOT.jar app.jar
RUN java -Djarmode=tools -jar app.jar extract --layers --launcher --destination dest

FROM eclipse-temurin:21-jre-jammy
WORKDIR /application
COPY --from=builder extracted/dest/dependencies/ ./
COPY --from=builder extracted/dest/spring-boot-loader/ ./
COPY --from=builder extracted/dest/snapshot-dependencies/ ./
COPY --from=builder extracted/dest/application/ ./

EXPOSE 8080

ENTRYPOINT ["java", "org.springframework.boot.loader.launch.JarLauncher"]
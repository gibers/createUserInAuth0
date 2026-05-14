# Stage de test
FROM eclipse-temurin:21-jdk-jammy AS test
WORKDIR /app
COPY .mvn/ ./.mvn/
COPY mvnw pom.xml .env ./
COPY src ./src
RUN chmod +x mvnw
CMD ["./mvnw", "clean", "test", "-Dspring.profiles.active=docker"]

FROM eclipse-temurin:21-jdk-jammy AS builder
WORKDIR /extracted
COPY libs/ ./libs/
COPY .mvn/ ./.mvn/
COPY mvnw pom.xml ./
RUN chmod +x mvnw && \
    ./mvnw install:install-file -Dfile=libs/feignCallsLib-0.0.1-SNAPSHOT.jar -DgroupId=com.oidccall -DartifactId=feignCallsLib -Dversion=0.0.1-SNAPSHOT -Dpackaging=jar && \
    ./mvnw install:install-file -Dfile=libs/restoCheckerDtos-0.0.1-SNAPSHOT.jar -DgroupId=com.oidccall -DartifactId=restoCheckerDtos -Dversion=0.0.1-SNAPSHOT -Dpackaging=jar
COPY src ./src
RUN chmod +x mvnw
RUN ./mvnw clean package -DskipTests
RUN java -Djarmode=tools -jar target/*.jar extract --layers --launcher --destination dest

FROM eclipse-temurin:21-jre-jammy
WORKDIR /application
COPY --from=builder extracted/dest/dependencies/ ./
COPY --from=builder extracted/dest/spring-boot-loader/ ./
COPY --from=builder extracted/dest/snapshot-dependencies/ ./
COPY --from=builder extracted/dest/application/ ./

EXPOSE 8443

ENTRYPOINT ["java", "org.springframework.boot.loader.launch.JarLauncher"]


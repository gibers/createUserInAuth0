# Stage dependencies
FROM eclipse-temurin:21-jdk-jammy AS dependencies
WORKDIR /workspace

COPY restoCheckerDtos/.mvn/ restoCheckerDtos/.mvn/
COPY restoCheckerDtos/mvnw restoCheckerDtos/pom.xml restoCheckerDtos/
COPY restoCheckerDtos/src restoCheckerDtos/src
RUN cd restoCheckerDtos && chmod +x mvnw && ./mvnw -B clean install -DskipTests

COPY feignCallsLib/.mvn/ feignCallsLib/.mvn/
COPY feignCallsLib/mvnw feignCallsLib/pom.xml feignCallsLib/
COPY feignCallsLib/src feignCallsLib/src
RUN cd feignCallsLib && chmod +x mvnw && ./mvnw -B clean install -DskipTests

# Stage de test
FROM dependencies AS test
WORKDIR /workspace/createUserInAuth0
COPY createUserInAuth0/.mvn/ ./.mvn/
COPY createUserInAuth0/mvnw createUserInAuth0/pom.xml ./
COPY createUserInAuth0/src ./src
RUN chmod +x mvnw
RUN ./mvnw clean test -Dspring.profiles.active=docker

# Stage builder
FROM dependencies AS builder
WORKDIR /workspace/createUserInAuth0
COPY createUserInAuth0/.mvn/ ./.mvn/
COPY createUserInAuth0/mvnw createUserInAuth0/pom.xml ./
COPY createUserInAuth0/src ./src
RUN chmod +x mvnw
RUN ./mvnw -B clean package -DskipTests
RUN java -Djarmode=tools -jar target/*.jar extract --layers --launcher --destination dest

# Stage final
FROM eclipse-temurin:21-jre-jammy as final
WORKDIR /application
COPY --from=builder /workspace/createUserInAuth0/dest/dependencies/ ./
COPY --from=builder /workspace/createUserInAuth0/dest/spring-boot-loader/ ./
COPY --from=builder /workspace/createUserInAuth0/dest/snapshot-dependencies/ ./
COPY --from=builder /workspace/createUserInAuth0/dest/application/ ./

EXPOSE 8443

ENTRYPOINT ["java", "org.springframework.boot.loader.launch.JarLauncher"]

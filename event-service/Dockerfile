# Multi-stage build: stage 1 compiles the jar, stage 2 runs it on a
# JRE-only image - final image doesn't carry Maven/build tools around,
# meaningfully smaller than a single-stage build.
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app

# Dependencies copied and resolved BEFORE the source code - Docker caches
# this layer separately, so CI rebuilds skip re-downloading dependencies
# unless pom.xml itself changes (the common case is source-only changes).
COPY pom.xml .
RUN mvn dependency:go-offline -B

COPY src ./src
RUN mvn clean package -DskipTests -B

FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
EXPOSE 8081
ENTRYPOINT ["java", "-jar", "app.jar"]

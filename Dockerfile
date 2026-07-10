# syntax=docker/dockerfile:1

########## Build stage ##########
FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /workspace

COPY pom.xml testng.xml ./
COPY src ./src

RUN mvn -B -q dependency:go-offline

########## Runtime stage ##########
FROM maven:3.9-eclipse-temurin-21
WORKDIR /workspace

COPY --from=build /root/.m2 /root/.m2
COPY pom.xml testng.xml ./
COPY src ./src

ENV BROWSER=chrome
ENV ENV=qa
ENV HEADLESS=true

ENTRYPOINT ["sh", "-c", "mvn -B test -Dbrowser=${BROWSER} -Denv=${ENV} -Dheadless=${HEADLESS}"]

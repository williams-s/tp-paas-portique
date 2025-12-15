FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package

FROM eclipse-temurin:21-jdk
WORKDIR /app
COPY --from=build /app/target/entrance-door-lock-1.0-SNAPSHOT-jar-with-dependencies.jar entrance-door-lock.jar

CMD ["java", "-jar", "entrance-door-lock.jar"]
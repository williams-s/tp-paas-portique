FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package

FROM eclipse-temurin:21-jdk
WORKDIR /app
COPY --from=build /app/target/badge-sensor-mock-1.0-SNAPSHOT-jar-with-dependencies.jar badge-sensor-mock.jar
EXPOSE 8080

CMD ["java", "-jar", "badge-sensor-mock.jar"]
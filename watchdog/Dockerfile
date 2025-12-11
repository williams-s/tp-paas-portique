FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package

FROM eclipse-temurin:21-jdk
WORKDIR /app
COPY --from=build /app/target/watchdog-0.0.1-SNAPSHOT.jar watchdog.jar
EXPOSE 8080

CMD ["java", "-jar", "watchdog.jar"]
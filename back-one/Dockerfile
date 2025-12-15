FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package

FROM eclipse-temurin:21-jdk
WORKDIR /app
COPY --from=build /app/target/back-one-0.0.1-SNAPSHOT.jar back-one.jar
EXPOSE 8080

CMD ["java", "-jar", "back-one.jar"]
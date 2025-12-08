# Use Maven (3.9.6) with Java 21 (Eclipse Temurin)
FROM maven:3.9.6-eclipse-temurin-21

WORKDIR /app

COPY pom.xml .
RUN mvn dependency:go-offline

COPY . .
RUN mvn package -DskipTests

CMD ["java", "-jar", "target/cache-loading-backend-0.0.1-SNAPSHOT.jar"]
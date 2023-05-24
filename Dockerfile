FROM maven:3.9.0-eclipse-temurin-17 AS build
COPY . .
RUN mvn clean package -Pprod -DskipTests

FROM eclipse-temurin:17-jdk-alpine

COPY --from=build ./target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","/app.jar"]
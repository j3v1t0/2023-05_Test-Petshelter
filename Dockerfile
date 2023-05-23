#mvn --version
FROM maven:3.6.3-openjdk-17 AS build
WORKDIR /app
COPY . /app/

#java --version
FROM  openjdk:17-alpine
WORKDIR /app
COPY --from=build /app/target/*.jar /app/app.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","app.jar"]
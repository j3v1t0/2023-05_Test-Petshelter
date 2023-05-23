#mvn --version
FROM maven:3.6.3-openjdk-17 AS build
WORKDIR /app
COPY . /home/app
RUN mvn -f /home/app/pom.xml clean package -DskipTests

#java --version
FROM  openjdk:17-alpine
VOLUME /tmp
COPY --from=build /home/app/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["sh", "-c", "java -jar /app.jar"]
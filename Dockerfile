FROM  amazoncorretto:17-alpine-jdk
MAINTAINER alfonsoalmonte
COPY target/petshelter-0.0.1-SNAPSHOT.jar petshelter-app.jar
ENTRYPOINT ["java","-jar","/petshelter-app.jar"]
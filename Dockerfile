FROM adoptopenjdk/openjdk11:latest

COPY target/*.jar /usr/src/joko-backend-starter-kit-SNAPSHOT.jar

CMD java -jar /usr/src/joko-backend-starter-kit-SNAPSHOT.jar
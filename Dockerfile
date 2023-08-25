FROM ubuntu:latest

RUN apt-get update && apt-get install -y default-jre

COPY build/libs/certis-0.0.1-SNAPSHOT.jar certis.jar
ENTRYPOINT ["java", "-jar", "/certis.jar"]
FROM openjdk:17-jdk-alpine

EXPOSE 8080

WORKDIR /api

COPY ./target/taskmanager-api-0.0.1-SNAPSHOT.jar ./taskmanager-api.jar
COPY ./wait-for-it.sh ./wait-for-it.sh

RUN apk add --no-cache bash

RUN chmod +x wait-for-it.sh

ENTRYPOINT ["/bin/sh", "-c", "./wait-for-it.sh postgres:5432 --timeout=60 -- java -jar taskmanager-api.jar"]
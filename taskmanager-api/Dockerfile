FROM adoptopenjdk:17-jre-hotspot

EXPOSE 8080

WORKDIR /api

COPY /target/taskmanager-api-0.0.1-SNAPSHOT.jar ./taskmanager-api.jar

RUN chmod +x wait-for-it.sh

CMD ["java", "-jar", "taskmanager-api.jar"]

ENTRYPOINT ["/bin/bash", "-c", "./wait-for-it.sh postgres:5432--timeout=60"]

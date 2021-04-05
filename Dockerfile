FROM openjdk:11-jre-slim
EXPOSE 8090
WORKDIR /app
COPY target/ussdprocessor-0.0.1-SNAPSHOT.jar .
ENTRYPOINT [ "java", "-jar", "ussdprocessor-0.0.1-SNAPSHOT.jar" ]
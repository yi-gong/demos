FROM adoptopenjdk/openjdk14:jdk-14.0.2_12
EXPOSE 8901/tcp
ARG JAR_FILE
COPY ${JAR_FILE} exchange-rate-service.jar
ENTRYPOINT ["java", "-jar", "/exchange-rate-service.jar"]

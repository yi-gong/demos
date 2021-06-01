FROM adoptopenjdk/openjdk14:jdk-14.0.2_12
EXPOSE 8099/tcp
EXPOSE 9090/tcp
ARG JAR_FILE
COPY ${JAR_FILE} currency-rest-service.jar
ENTRYPOINT ["java", "-jar", "/currency-rest-service.jar"]

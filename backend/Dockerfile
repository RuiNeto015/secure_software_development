FROM openjdk:17.0-jdk-oracle
COPY target/*.jar app.jar
COPY .env .
ENTRYPOINT ["java","-jar","/app.jar"]
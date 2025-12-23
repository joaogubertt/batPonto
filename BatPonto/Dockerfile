FROM eclipse-temurin:17-jdk-alpine

WORKDIR /app

VOLUME /tmp

COPY target/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "/app/app.jar"]
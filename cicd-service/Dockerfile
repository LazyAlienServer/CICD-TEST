FROM eclipse-temurin:21-jre-alpine

WORKDIR /app
COPY cicd-service/target/cicd-service-0.1.0-SNAPSHOT.jar app.jar

EXPOSE 8082
ENTRYPOINT ["java", "-jar", "/app/app.jar"]

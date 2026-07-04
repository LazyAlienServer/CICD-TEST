FROM eclipse-temurin:21-jre-alpine

WORKDIR /app
COPY gateway-service/target/gateway-service-0.1.0-SNAPSHOT.jar app.jar

EXPOSE 9090
ENTRYPOINT ["java", "-jar", "/app/app.jar"]

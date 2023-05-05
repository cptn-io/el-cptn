FROM eclipse-temurin:17-jdk-alpine

LABEL org.opencontainers.image.description="Management Service"

ARG JAR_FILE

COPY ${JAR_FILE} /app.jar

ENTRYPOINT ["java","-jar","/app.jar"]
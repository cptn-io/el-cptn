FROM eclipse-temurin:17-jdk-alpine

LABEL org.opencontainers.image.description="Management Service"

ARG JAR_FILE

RUN addgroup -S spring && adduser -S spring -G spring

RUN mkdir -p /logs
RUN chown spring /logs

USER spring:spring

COPY ${JAR_FILE} /app.jar

ENTRYPOINT ["java","-jar","/app.jar"]
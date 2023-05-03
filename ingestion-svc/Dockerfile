FROM eclipse-temurin:17-jdk-alpine

RUN addgroup -S spring && adduser -S spring -G spring

RUN mkdir -p /logs
RUN chown spring /logs

USER spring:spring

COPY target/*.jar app.jar

ENTRYPOINT ["java","-jar","/app.jar"]
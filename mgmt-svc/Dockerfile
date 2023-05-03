FROM eclipse-temurin:17-jdk-alpine

RUN addgroup -S spring && adduser -S spring -G spring

RUN mkdir -p /logs
RUN mkdir -p /tmp/apps

RUN chown spring /logs
RUN chown spring /tmp/apps

USER spring:spring

COPY target/*.jar app.jar

ENTRYPOINT ["java","-jar","/app.jar"]
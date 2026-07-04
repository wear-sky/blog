FROM eclipse-temurin:17-jre

WORKDIR /log-service

COPY target/log-service-1.0.0.jar log-service-1.0.0.jar

EXPOSE 58080

ENTRYPOINT ["java", "-jar", "log-service-1.0.0.jar"]

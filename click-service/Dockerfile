FROM eclipse-temurin:17-jre

WORKDIR /click-service

COPY target/click-service-1.0.0.jar click-service-1.0.0.jar

EXPOSE 48080

ENTRYPOINT ["java", "-jar", "click-service-1.0.0.jar"]

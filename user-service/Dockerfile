FROM eclipse-temurin:17-jre

WORKDIR /user-service

COPY target/user-service-1.0.0.jar user-service-1.0.0.jar

EXPOSE 28080

ENTRYPOINT ["java", "-jar", "user-service-1.0.0.jar"]

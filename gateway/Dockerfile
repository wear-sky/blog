FROM eclipse-temurin:17-jre

WORKDIR /gateway

COPY target/gateway-1.0.0.jar gateway-1.0.0.jar

EXPOSE 18080

ENTRYPOINT ["java", "-jar", "gateway-1.0.0.jar"]

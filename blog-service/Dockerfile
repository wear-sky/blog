FROM eclipse-temurin:17-jre

WORKDIR /blog-service

COPY target/blog-service-1.0.0.jar blog-service-1.0.0.jar

EXPOSE 38080

ENTRYPOINT ["java", "-jar", "blog-service-1.0.0.jar"]

FROM openjdk:17
COPY build/libs/popolog-apigateway-service.jar popolog-apigateway-service.jar
ENTRYPOINT ["java", "-jar", "/popolog-discovery-service.jar"]
FROM openjdk:17
COPY build/libs/popolog-apigateway-service.jar popolog-apigateway-service.jar
ENV TZ=Asia/Seoul
ENTRYPOINT ["java", "-jar", "/popolog-apigateway-service.jar"]
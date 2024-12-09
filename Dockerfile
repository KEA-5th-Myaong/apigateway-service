FROM openjdk:17
COPY build/libs/popolog-apigateway-service.jar popolog-apigateway-service.jar
ENV TZ=Asia/Seoul
ENTRYPOINT ["java", "-jar", "/popolog-apigateway-service.jar"]

# Docker run 명령을 통해 다른 인자를 지정할 경우 덮어쓸 수 있음
CMD ["--server.port=8000"]
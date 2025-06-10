FROM openjdk:21-jdk-slim
WORKDIR /app
COPY target/java-ecr-demo.jar app.jar
ENTRYPOINT ["java","-jar","app.jar"]
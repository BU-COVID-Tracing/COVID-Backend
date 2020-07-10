FROM openjdk:8-jdk-alpine
RUN addgroup -S spring && adduser -S spring -G spring
USER spring:spring
ARG RUN_MODE="SQLKeySet"
ENV MY_RUN_MODE=${RUN_MODE}
ARG JAR_FILE=target/*.jar
EXPOSE 8080
COPY ${JAR_FILE} app.jar
ENTRYPOINT java -jar /app.jar ${MY_RUN_MODE}

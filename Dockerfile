FROM openjdk:18
WORKDIR /app
COPY ./target/myapp.jar /app/myapp.jar
ENTRYPOINT ["java", "-jar", "myapp.jar"]
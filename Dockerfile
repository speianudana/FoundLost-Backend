FROM openjdk:11

WORKDIR /spring-boot-app

COPY . .

RUN ./mvnw clean install

EXPOSE 8080

ENTRYPOINT ["java","-jar", "target/foundlost.jar"]
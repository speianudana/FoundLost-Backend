FROM openjdk:11

WORKDIR /spring-boot-app

ADD . .

RUN chmod +x -R .

# RUN ./mvnw spring-boot:run

RUN ./mvnw clean install
RUN chmod +x -R ./target

EXPOSE 8080

ENTRYPOINT ["java","-jar", "target/foundlost.jar"]
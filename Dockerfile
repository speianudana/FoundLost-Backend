FROM openjdk:15
ADD target/foundlost.jar foundlost.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","foundlost.jar"]
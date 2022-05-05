FROM maven:3.8.5-openjdk-11 as build
ENV HOME=/usr/app
RUN mkdir -p $HOME
WORKDIR $HOME
RUN mvn verify --fail-never
ADD . $HOME
RUN mvn clean install

FROM openjdk:11-jdk
COPY --from=build /usr/app/target/foundlost.jar /app/runner.jar
ENTRYPOINT ["java", "-jar", "/app/runner.jar"]
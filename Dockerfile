FROM maven:3.8.4-openjdk-17-slim as builder
WORKDIR /app
COPY . /app/.
RUN mvn -f /app/pom.xml clean package -Dmaven.test.skip=true

FROM eclipse-temurin:17-jre
WORKDIR /app

COPY --from=builder /app/target/messagesCounterBot-1.0.jar app.jar

ENTRYPOINT ["java", "-jar", "app.jar", "-Dspring.profiles.active=prom"]


# FROM eclipse-temurin:17-jre
# WORKDIR /app
#
# COPY  /target/messagesCounterBot-1.0.jar app.jar
# # COPY /src/main/resources/photo/img.png img.png
#
# ENTRYPOINT ["java", "-jar", "app.jar", "-Dspring.profiles.active=prom"]
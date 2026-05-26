# 1) build stage
FROM gradle:8.5-jdk17 AS build
WORKDIR /app
COPY . .
RUN chmod +x ./gradlew
RUN ./gradlew :GateWay:clean :GateWay:build -x test

# 2) run stage
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
COPY --from=build /app/GateWay/build/libs/*.jar app.jar

EXPOSE 8080
ENV SPRING_PROFILES_ACTIVE=prod
ENTRYPOINT ["java", "-Dsun.net.inetaddr.ttl=10", "-jar", "app.jar"]

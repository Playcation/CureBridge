# 1) build stage
FROM gradle:8.5-jdk17 AS build
WORKDIR /app
COPY . .
RUN gradle :GateWay:clean :GateWay:build -x test

# 2) run stage
FROM eclipse-temurin:17-jdk
WORKDIR /app
COPY --from=build /app/GateWay/build/libs/*.jar app.jar

EXPOSE 8084
ENTRYPOINT ["java","-jar","app.jar"]
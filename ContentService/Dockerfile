# 1) build stage
FROM gradle:8.5-jdk17 AS build
WORKDIR /app
COPY . .
RUN gradle :ContentService:clean :ContentService:build -x test

# 2) run stage
FROM eclipse-temurin:17-jdk
WORKDIR /app
COPY --from=build /app/ContentService/build/libs/*.jar app.jar

EXPOSE 8082
ENV SPRING_PROFILES_ACTIVE=prod
ENTRYPOINT ["java","-jar","app.jar"]
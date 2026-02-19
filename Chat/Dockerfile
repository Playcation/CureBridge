# 1) build stage
FROM gradle:8.5-jdk17 AS build
WORKDIR /app
COPY . .
RUN gradle :Chat:clean :Chat:build -x test

# 2) run stage
FROM eclipse-temurin:17-jdk
WORKDIR /app
COPY --from=build /app/Chat/build/libs/*.jar app.jar

EXPOSE 8083
ENTRYPOINT ["java","-jar","app.jar"]
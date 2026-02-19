# 1) build stage
FROM gradle:8.5-jdk17 AS build
WORKDIR /app
COPY . .
RUN gradle :AppService:clean :AppService:build -x test

# 2) run stage
FROM eclipse-temurin:17-jdk
WORKDIR /app
COPY --from=build /app/AppService/build/libs/*.jar app.jar

# AppService 포트는 설정에 맞게 수정해줘 (기본값 8080일 가능성 큼)
EXPOSE 8080
ENTRYPOINT ["java","-jar","app.jar"]
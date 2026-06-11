# 1) build stage
FROM gradle:8.5-jdk17 AS build
WORKDIR /app
COPY . .
RUN chmod +x ./gradlew && ./gradlew :ContentService:clean :ContentService:build -x test --parallel
# 2) run stage
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
RUN apk add --no-cache tzdata && \
    cp /usr/share/zoneinfo/Asia/Seoul /etc/localtime && \
    echo "Asia/Seoul" > /etc/timezone
COPY --from=build /app/ContentService/build/libs/*-SNAPSHOT.jar app.jar

EXPOSE 8082
ENV SPRING_PROFILES_ACTIVE=prod
ENTRYPOINT ["java", "-Dsun.net.inetaddr.ttl=10", "-Duser.timezone=Asia/Seoul", "-jar", "app.jar"]
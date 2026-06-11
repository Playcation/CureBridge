FROM gradle:8.5-jdk17 AS build
WORKDIR /app
COPY . .
RUN chmod +x ./gradlew
RUN chmod +x ./gradlew && ./gradlew :MemberService:clean :MemberService:build -x test --parallel

FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
RUN apk add --no-cache tzdata && \
    cp /usr/share/zoneinfo/Asia/Seoul /etc/localtime && \
    echo "Asia/Seoul" > /etc/timezone
COPY --from=build /app/MemberService/build/libs/*-SNAPSHOT.jar app.jar

EXPOSE 8081
ENV SPRING_PROFILES_ACTIVE=prod
ENTRYPOINT ["java", "-Dsun.net.inetaddr.ttl=10", "-Duser.timezone=Asia/Seoul", "-jar", "app.jar"]
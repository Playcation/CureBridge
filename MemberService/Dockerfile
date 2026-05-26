FROM gradle:8.5-jdk17 AS build
WORKDIR /app
COPY . .
RUN chmod +x ./gradlew
RUN ./gradlew :MemberService:clean :MemberService:build -x test

FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

COPY --from=build /app/MemberService/build/libs/*.jar app.jar

EXPOSE 8081
ENV SPRING_PROFILES_ACTIVE=prod
ENTRYPOINT ["java", "-Dsun.net.inetaddr.ttl=10", "-jar", "app.jar"]

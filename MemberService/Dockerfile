FROM gradle:8.5-jdk17 AS build
WORKDIR /app
COPY . .
RUN chmod +x ./gradlew
RUN ./gradlew :MemberService:clean :MemberService:build -x test --no-daemon --stacktrace --info

FROM eclipse-temurin:17-jdk
WORKDIR /app
COPY --from=build /app/MemberService/build/libs/*.jar app.jar

EXPOSE 8081
ENV SPRING_PROFILES_ACTIVE=prod
ENTRYPOINT ["java","-jar","app.jar"]

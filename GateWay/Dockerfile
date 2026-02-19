# 1) build stage
RUN chmod +x ./gradlew
RUN ./gradlew :GateWay:clean :GateWay:build -x test --no-daemon --stacktrace --info
# 2) run stage
FROM eclipse-temurin:17-jdk
WORKDIR /app
COPY --from=build /app/GateWay/build/libs/*.jar app.jar

EXPOSE 8084
ENTRYPOINT ["java","-jar","app.jar"]

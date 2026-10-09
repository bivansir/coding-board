FROM eclipse-temurin:26-jdk AS build
WORKDIR /app
COPY gradlew settings.gradle build.gradle ./
COPY gradle ./gradle
COPY src ./src
RUN ./gradlew --no-daemon bootJar -x test

FROM eclipse-temurin:26-jre
WORKDIR /app
COPY --from=build /app/build/libs/app.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
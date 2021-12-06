FROM eclipse-temurin:17

WORKDIR /app
COPY gradle gradlew settings.gradle.kts build.gradle.kts /app/
RUN chmod +x gradlew
RUN ./gradlew build || return 0

COPY . .
RUN ./gradlew clean build --console=plain --info

FROM eclipse-temurin:17
COPY --from=0 /app/build/libs/*.jar app.jar
CMD java -Xms64m -Xmx512m -jar app.jar

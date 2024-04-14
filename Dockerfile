#FROM adoptopenjdk:15-jdk
#WORKDIR /app
#
#COPY gradlew /app
#COPY gradle /app/gradle
#RUN chmod +x gradlew
#RUN ./gradlew
#
#COPY . .
#RUN ./gradlew clean assemble --console=plain --info

FROM bellsoft/liberica-openjdk-alpine:21
WORKDIR /app
#COPY --from=0 /app/build/libs/*.jar app.jar
COPY build/libs/*.jar app.jar
CMD java -Xms64m -Xmx128m -jar app.jar

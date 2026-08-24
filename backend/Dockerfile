FROM eclipse-temurin:21-jdk-alpine AS build
WORKDIR /workspace
COPY .mvn .mvn
COPY mvnw pom.xml ./
RUN chmod +x mvnw && ./mvnw -B dependency:go-offline
COPY src src
RUN ./mvnw -B clean package

FROM eclipse-temurin:21-jre-alpine
RUN addgroup -S asthma && adduser -S asthma -G asthma
WORKDIR /app
COPY --from=build /workspace/target/asthmatracker-0.0.1-SNAPSHOT.jar app.jar
USER asthma
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app/app.jar"]

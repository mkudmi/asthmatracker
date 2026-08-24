FROM openjdk:21-jdk-slim

WORKDIR /app

# Копируем уже собранный jar-файл
COPY target/asthmatracker-0.0.1-SNAPSHOT.jar asthmatracker.jar

# Открываем порт 8080
EXPOSE 8080

# Запуск приложения
ENTRYPOINT ["java", "-jar", "asthmatracker.jar"]


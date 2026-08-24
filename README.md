# Asthma Tracker

Единый локальный репозиторий проекта:

- `backend/` — Spring Boot API из [BackEnd-Asthma_Tracker](https://github.com/AlexDesmos/BackEnd-Asthma_Tracker);
- `frontend/` — React-приложение из [FrontEnd-Asthma_Tracker](https://github.com/AlexDesmos/FrontEnd-Asthma_Tracker).

## Локальный запуск через Docker

База и backend запускаются одной командой. Flyway сам создаст схему БД:

```bash
docker compose -f backend/docker-compose.yml up --build
```

API будет доступен на <http://localhost:8080>.

Frontend (во втором терминале):

```bash
cd frontend
npm ci
REACT_APP_API_URL=http://localhost:8080/api npm start
```

Приложение откроется на <http://localhost:3000>.

## Запуск backend без Docker

Требуются Java 21 и PostgreSQL. Параметры передаются только через окружение:

```bash
cd backend
DB_URL=jdbc:postgresql://localhost:5432/asthmatracker \
DB_USERNAME=asthmauser \
DB_PASSWORD=asthmapassword \
./mvnw spring-boot:run
```

## Проверка

```bash
cd backend && ./mvnw clean test
cd ../frontend && npm run build
```

Backend-тесты используют in-memory H2 и не требуют внешней БД.

## Обновление из исходных репозиториев

Исходные репозитории подключены как `backend-upstream` и `frontend-upstream`. Их история сохранена через Git subtree. После pull нужно вручную проверить конфликты с текущей архитектурой.

```bash
git subtree pull --prefix=backend backend-upstream master
git subtree pull --prefix=frontend frontend-upstream master
```

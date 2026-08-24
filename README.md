# Asthma Tracker

Единый локальный репозиторий проекта:

- `backend/` — Spring Boot API из [BackEnd-Asthma_Tracker](https://github.com/AlexDesmos/BackEnd-Asthma_Tracker);
- `frontend/` — React-приложение из [FrontEnd-Asthma_Tracker](https://github.com/AlexDesmos/FrontEnd-Asthma_Tracker).

## Локальный запуск

Требуются Java 21, Node.js/npm и доступная PostgreSQL.

Backend (после настройки PostgreSQL):

```bash
cd backend
SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/asthmatracker \
SPRING_DATASOURCE_USERNAME=asthmauser \
SPRING_DATASOURCE_PASSWORD=asthmapassword \
./mvnw spring-boot:run
```

Frontend (во втором терминале):

```bash
cd frontend
npm ci
REACT_APP_API_URL=http://localhost:8080 npm start
```

Приложение откроется на <http://localhost:3000>.

> В исходном backend адрес и учётные данные PostgreSQL заданы в `application.yml` и в конфигурации jOOQ в `pom.xml`. Перед локальной сборкой нужно перевести оба места на отдельную локальную БД; переменные в команде выше переопределяют runtime-конфигурацию, но не Maven-плагин jOOQ.

## Обновление из исходных репозиториев

Исходные репозитории подключены как `backend-upstream` и `frontend-upstream`. Их история сохранена через Git subtree.

```bash
git subtree pull --prefix=backend backend-upstream master
git subtree pull --prefix=frontend frontend-upstream master
```

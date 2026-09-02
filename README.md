# Asthma Tracker

Единый локальный репозиторий проекта:

- `backend/` — Spring Boot API из [BackEnd-Asthma_Tracker](https://github.com/AlexDesmos/BackEnd-Asthma_Tracker);
- `frontend/` — React-приложение из [FrontEnd-Asthma_Tracker](https://github.com/AlexDesmos/FrontEnd-Asthma_Tracker).

## Локальный запуск через Docker

Весь стек запускается одной командой из корня репозитория:

```bash
docker compose up --build -d
```

Поднимутся сразу:

- `postgres` — локальная PostgreSQL с постоянным volume `asthma-postgres-data`;
- `api` — Spring Boot backend с миграциями Flyway;
- `frontend` — production-сборка React через `nginx`, который проксирует `/api` в backend.

После запуска:

- приложение доступно на <http://localhost>;
- backend доступен на <http://localhost:8080>;
- PostgreSQL доступна на `localhost:5432`.

Настройки можно переопределить через `.env`. Шаблон лежит в `.env.example`.

Для локального Docker-запуска автоматически создаются и восстанавливаются при каждом старте API тестовые учётные записи:

| Роль | Логин | Пароль |
| --- | --- | --- |
| Пациент | ОМС `0000000000000000` | `admin` |
| Врач | Табельный номер `admind` | `admin` |

Инициализация включается переменной `DEMO_USERS_ENABLED=true`. Для production-окружения установите `DEMO_USERS_ENABLED=false`.

Продовый адрес API больше не захардкожен во фронтенде. Если нужен внешний backend или внешняя БД, задавайте значения через переменные окружения, а не через код.

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

## Продовая БД

Сейчас безопасная схема такая:

- локальная БД всегда поднимается вместе с приложением через `docker compose`;
- продовая БД не используется по умолчанию, пока для неё не будет выдан корректный `DB_URL`.

Если понадобится подключить внешний production PostgreSQL, достаточно передать корректные `DB_URL`, `DB_USERNAME` и `DB_PASSWORD` для сервиса `api`.

## Обновление из исходных репозиториев

Исходные репозитории подключены как `backend-upstream` и `frontend-upstream`. Их история сохранена через Git subtree. После pull нужно вручную проверить конфликты с текущей архитектурой.

```bash
git subtree pull --prefix=backend backend-upstream master
git subtree pull --prefix=frontend frontend-upstream master
```

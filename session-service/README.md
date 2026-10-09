# Session Service
Микросервис управления процессом интервью: сессии, кандидаты, задачи, фидбек.

## Стек
- Java 26, Spring Boot 4, Lombok, Gradle
- PostgreSQL 16 (+pg_trgm)
- Liquibase
- Springdoc
- JUnit 5
- Testcontainers

## Описание МС
### Содержание
Содержит в себе функционал для управления сущностями бизнес-процесса интервью и API:
- Candidate
- Position
- Task
- Session
  - Note (агрегат Session)
  - SessionTask (агрегат Session)
  - SessionCancellation (агрегат Session)
- Feedback
  - FeedbackScore (агрегат Feedback)
### Особенности архитектуры
- Package-by-feature
- Контроллеры разбиты по агрегатам; зависимые сущности доступны через вложенные пути
- Поиск по ФИО Candidate: pg_trgm (нечёткий поиск, устойчив к опечаткам)
- Поиск по названию Task: полнотекстовый поиск PostgreSQL (tsvector, русский + английский)
### API
Swagger UI: http://localhost:8080/swagger-ui.html

## Запуск локально
### Всё в Docker (Docker)
1. `cp .env.example .env`
2. `docker compose up --build`

### Сервис из IDE/Gradle, БД в Docker (Docker + JDK 26)
1. `cp .env.example .env`
2. `docker compose up -d postgres`
3. `./gradlew bootRun`

## Тестирование
- `./gradlew test -PunitOnly` — только unit-тесты (Docker не нужен)
- `./gradlew test` — все тесты (нужен запущенный Docker)
Интеграционные тесты наследуются от `AbstractIntegrationTest`

## Миграции
Liquibase changelog: `src/main/resources/db/changelog/`  
- Применяются автоматически при старте  
- Миграции создают расширение `pg_trgm`

## Переменные окружения
Шаблон: `.env.example`  
|Переменная|Дефолт|
|---|---|
|`DB_URL`|`jdbc:postgresql://localhost:5432/session_service`|
|`DB_NAME`|`session_service`|
|`DB_USERNAME`|`postgres`|
|`DB_PASSWORD`|`postgres`|
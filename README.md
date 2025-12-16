# Lab 10 – HTTP with Spring Boot (SQLite + Flyway)

## Tech stack
- Java 17
- Spring Boot (Web, Security, Data JPA, Validation)
- SQLite
- Flyway

## Requirements
- Java 17+
- Git
- (optional) Maven wrapper included: `./mvnw`

## Setup
1. Create `.env` file in the project root (use `.env.example`):
    - DB_URL=jdbc:sqlite:database.db
    - DB_USERNAME=
    - DB_PASSWORD=

2. Run the application:
   ./mvnw spring-boot:run

## Database
- SQLite file: `database.db` (not committed)
- Flyway migrations: `src/main/resources/db/migration`
- Current migration: `V1__create_users_table.sql` creates `users` table

## Endpoints
- GET `/hello` → returns `OK` (public)

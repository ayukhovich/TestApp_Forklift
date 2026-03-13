# Справочник Погрузчиков

Веб-приложение для управления парком погрузчиков и учета их простоев.

## Описание

Приложение позволяет:
- Управлять справочником погрузчиков (добавление, редактирование, удаление)
- Регистрировать простои погрузчиков с автоматическим расчетом времени
- Осуществлять поиск по номеру погрузчика

## Технологии

- Java 17+
- Spring Boot 3.2
- PostgreSQL 14+
- Vanilla jQuery

## Запуск

### Требования
- Java 17+
- Maven 3.8+
- PostgreSQL 14+

### Сборка
```bash
mvn clean package
```

### Запуск приложения
```bash
mvn spring-boot:run
```

Приложение доступно по адресу: http://localhost:8080

### Docker Compose

```bash
docker-compose up --build
```

### Загрузка начальных данных

После первого запуска необходимо загрузить начальные данные:

```bash
psql -U postgres -d forkliftdb -f src/main/resources/load-data.sql
```

или через docker-compose:

```bash
docker-compose exec -T postgres psql -U postgres -d forkliftdb -f /docker-entrypoint-initdb.d/load-data.sql
```

## Тестирование

```bash
mvn test
mvn test -Dtest=ForkliftServiceTest
```

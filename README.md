# Bulls and Cows Game

## Описание

Это игра "Быки и Коровы", в которой игрок пытается угадать 4-значное число с неповторяющимися цифрами.

## Требования

- Java 11+
- Maven
- PostgreSQL
- Node.js и npm

## Установка и запуск

### Backend

1. Клонируйте репозиторий:

    ```bash
    git clone "https://github.com/Dellgin/Summer_project_game"
    ```

2. Импортируйте dump.sql в PostgreSQL.

3. Настройте файл `application.properties`:

    ```
    spring.datasource.url=jdbc:postgresql://localhost:5432/bulls_and_cows
    spring.datasource.username=<your_username>
    spring.datasource.password=<your_password>
    spring.jpa.hibernate.ddl-auto=update
    ```

4. Соберите и запустите backend:

    ```bash
    mvn clean install
    mvn spring-boot:run
    ```

### Frontend

1. Перейдите в директорию фронтенда:

    ```bash
    cd front
    ```

2. Установите зависимости:

    ```bash
    npm install
    ```

3. Запустите фронтенд:

    ```bash
    npm start
    ```

### Для настройки конфигурации игры измените значения в `application.yml`:

   ```yaml
   game:
     unlimited: false(игра с ограничениями) или true(игра без ограничений)
     maxAttempts: 10
     timeLimit: 300 # Время в секундах
   ```

Если ваш frontend и backend работают на разных портах (например, 3000 и 8080), добавьте прокси в `package.json` фронтенда:

```json
"proxy": "http://localhost:8080"

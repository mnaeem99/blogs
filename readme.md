
# Blogs Backend Application

## Overview

This is a Spring Boot backend application for managing blog posts. It includes functionalities for user management, blog posts, comments, categories, tags, and likes. The application uses Spring Data JPA for database interactions, and it provides RESTful APIs for all the entities.

## Technologies Used

- Java 1.8
- Spring Boot 2.2.5
- Spring Data JPA
- Spring Data REST
- Spring Boot Actuator
- SpringDoc OpenAPI
- Liquibase
- PostgreSQL
- H2 Database (for testing)
- QueryDSL
- Lombok
- MapStruct
- JUnit
- Testcontainers

## Requirements

- Java 1.8+
- Maven 3.6.0+
- PostgreSQL 9.6+

## Setup and Installation

1. **Clone the repository:**
   ```sh
   git clone https://github.com/mnaeem99/blogs.git
   cd blogs
   ```

2. **Configure the database:**
   Update the `src/main/resources/application.properties` file with your PostgreSQL database configuration:
   ```properties
   spring.datasource.url=jdbc:postgresql://localhost:5432/your-database
   spring.datasource.username=your-username
   spring.datasource.password=your-password
   spring.jpa.hibernate.ddl-auto=update
   spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.PostgreSQLDialect
   ```

3. **Run Liquibase to set up the database schema:**
   ```sh
   mvn liquibase:update
   ```

4. **Build and run the application:**
   ```sh
   mvn clean install
   mvn spring-boot:run
   ```

## Testing

To run the tests, use the following command:
```sh
mvn test
```

## API Documentation

API documentation is generated using SpringDoc OpenAPI. Once the application is running, you can access the API documentation at:
```
http://localhost:5555/swagger-ui.html
```

## Database Schema

The application uses the following database schema:

### Users Table
```sql
CREATE TABLE users (
    user_id SERIAL PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    email VARCHAR(100) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

### Posts Table
```sql
CREATE TABLE posts (
    post_id SERIAL PRIMARY KEY,
    author_id INT NOT NULL,
    title VARCHAR(255) NOT NULL,
    content TEXT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (author_id) REFERENCES users(user_id) ON DELETE CASCADE
);
```

### Comments Table
```sql
CREATE TABLE comments (
    comment_id SERIAL PRIMARY KEY,
    post_id INT NOT NULL,
    author_id INT NOT NULL,
    content TEXT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (post_id) REFERENCES posts(post_id) ON DELETE CASCADE,
    FOREIGN KEY (author_id) REFERENCES users(user_id) ON DELETE CASCADE
);
```

### Categories Table
```sql
CREATE TABLE categories (
    category_id SERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE,
    description TEXT
);
```

### Post Categories Table
```sql
CREATE TABLE post_categories (
    post_id INT NOT NULL,
    category_id INT NOT NULL,
    PRIMARY KEY (post_id, category_id),
    FOREIGN KEY (post_id) REFERENCES posts(post_id) ON DELETE CASCADE,
    FOREIGN KEY (category_id) REFERENCES categories(category_id) ON DELETE CASCADE
);
```

### Tags Table
```sql
CREATE TABLE tags (
    tag_id SERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE
);
```

### Post Tags Table
```sql
CREATE TABLE post_tags (
    post_id INT NOT NULL,
    tag_id INT NOT NULL,
    PRIMARY KEY (post_id, tag_id),
    FOREIGN KEY (post_id) REFERENCES posts(post_id) ON DELETE CASCADE,
    FOREIGN KEY (tag_id) REFERENCES tags(tag_id) ON DELETE CASCADE
);
```

### Likes Table
```sql
CREATE TABLE likes (
    like_id SERIAL PRIMARY KEY,
    user_id INT NOT NULL,
    post_id INT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE,
    FOREIGN KEY (post_id) REFERENCES posts(post_id) ON DELETE CASCADE,
    UNIQUE (user_id, post_id)
);
```

## Contributing

1. Fork the repository.
2. Create a new feature branch (`git checkout -b feature/your-feature`).
3. Commit your changes (`git commit -am 'Add some feature'`).
4. Push to the branch (`git push origin feature/your-feature`).
5. Create a new Pull Request.

## License

This project is licensed under the MIT License.

---
```
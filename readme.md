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
   cd blogs-backend

2. **Build and run the application:**
   ```sh
    mvn clean install
    mvn spring-boot:run

3. **API Documentation:**

    http://localhost:5555/swagger-ui/index.html
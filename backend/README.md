# Dayflow — Human Resource Management System (Backend)

Backend service for the Dayflow Human Resource Management System (HRMS) built with Spring Boot 3.x and Java 21.

## Tech Stack
- **Java 21**
- **Spring Boot 3.x**
- **Spring Web**
- **Spring Data JPA**
- **Spring Security**
- **Jakarta Bean Validation**
- **PostgreSQL / Supabase PostgreSQL**
- **Flyway**
- **Springdoc OpenAPI (Swagger)**
- **Lombok**
- **JUnit 5 / MockMvc / Mockito**

## Project Structure
```text
dayflow-backend/
├── pom.xml
├── README.md
├── .gitignore
├── .env.example
└── src/
    ├── main/
    │   ├── java/com/dayflow/hrms/
    │   │   ├── DayflowApplication.java
    │   │   ├── config/
    │   │   ├── controller/
    │   │   ├── dto/
    │   │   ├── entity/
    │   │   ├── exception/
    │   │   ├── mapper/
    │   │   ├── repository/
    │   │   ├── security/
    │   │   ├── service/
    │   │   └── util/
    │   └── resources/
    │       └── application.yml
    └── test/
        └── java/com/dayflow/hrms/
```

## Getting Started

### Prerequisites
- JDK 21+
- Apache Maven 3.9+

### Environment Setup
Copy `.env.example` to `.env` (or supply environment variables):
```bash
cp .env.example .env
```

### Build and Test
```bash
mvn clean test
mvn clean package
```

### Run Locally
```bash
mvn spring-boot:run
```

### Health Check Endpoint
```http
GET http://localhost:8080/api/v1/health
```

Expected Response:
```json
{
  "status": "UP",
  "service": "dayflow-backend"
}
```

### Swagger Documentation
```http
http://localhost:8080/swagger-ui/index.html
http://localhost:8080/v3/api-docs
```

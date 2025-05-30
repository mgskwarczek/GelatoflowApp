# GelatoFlowApp

Aplikacja składa się z dwóch modułów:

- **backend** – Spring Boot REST API z JPA, JWT, Spring Security, Flyway
- **frontend** – React + TypeScript + Vite + Material-UI

---

## Backend

### Technologie

- Java 22, Spring Boot 3.3
- Spring Data JPA (Hibernate)
- Spring Security + JWT
- SpringDoc OpenAPI (Swagger UI)
- Flyway (opcjonalnie; w `application.yaml` wyłączony)
- Log4j2

### Uruchomienie

1. Skonfiguruj połączenie z bazą w `backend/src/main/resources/application.yaml`  
2. W katalogu głównym projektu uruchom:
   ```bash
   ./mvnw clean install
   ./mvnw -pl backend spring-boot:run


Aplikacja startuje na http://localhost:8080
Swagger UI
JSON spec: http://localhost:8080/v3/api-docs
UI: http://localhost:8080/swagger-ui.html

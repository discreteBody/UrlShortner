# UrlShortner

A lightweight Spring Boot service that creates short URLs and redirects short codes to the original URLs.

This repository contains:
- A REST endpoint to create short URLs from original URLs
- A redirect endpoint to map short codes back to original URLs
- JPA entity and repository for storage
- Basic validation for incoming requests
- Example DTOs and a small service layer

Tech stack
- Java 21
- Spring Boot 4.x
- Spring Data JPA
- ModelMapper
- Lombok

Quick start (development)
1. Clone the repo:
   ```bash
   git clone <your-repo-url>
   ```
2. From project root run with the included Maven wrapper:
   - On PowerShell / Windows:
     ```powershell
     cd D:\SpringBoot\Projects\UrlShortner
     .\mvnw.cmd spring-boot:run
     ```
   - On bash / macOS / Linux:
     ```bash
     ./mvnw spring-boot:run
     ```
3. Application will start on port 8080 by default.

Build a production jar
```bash
./mvnw -DskipTests package
```
Resulting artifact: `target/UrlShortner-0.0.1-SNAPSHOT.jar`

Configuration
- The service reads configuration from `src/main/resources/application.properties` (or environment).
- Important property:
  - `app.base-url` — base URL used when building returned short URLs (example: `http://short.url/`)

Example `application.properties` snippet:
```
# Base URL used in generated shortUrl responses
app.base-url=http://short.url/
# JPA/Hibernate example (use your DB)
spring.datasource.url=jdbc:postgresql://localhost:5432/urlshortner
spring.datasource.username=youruser
spring.datasource.password=yourpassword
spring.jpa.hibernate.ddl-auto=update
```

API

1) Create a short URL
- Method: POST
- Path: `/create`
- Content-Type: `application/json`
- Request body (JSON):
  ```json
  { "originalUrl": "https://example.com/very/long/path" }
  ```
- Example (curl):
  ```bash
  curl -X POST http://localhost:8080/create \
    -H "Content-Type: application/json" \
    -d '{"originalUrl":"https://example.com/very/long/path"}'
  ```
- Example (PowerShell):
  ```powershell
  Invoke-RestMethod -Method Post -Uri http://localhost:8080/create -ContentType 'application/json' -Body '{"originalUrl":"https://example.com/very/long/path"}'
  ```
- Successful response: HTTP 200 OK + JSON matching `UrlShortResponseDto`:
  ```json
  {
    "shortUrl": "http://short.url/abc123",
    "originalUrl": "https://example.com/very/long/path",
    "creationDate": "2026-01-16T12:34:56.789",
    "expirationDate": "2026-02-15T12:34:56.789"
  }
  ```

Validation & error cases for POST `/create`
- `originalUrl` is required and must not be blank (validation -> 400 Bad Request).
- `originalUrl` maximum length: 2048 characters (validation -> 400).
- If you send `text/plain` instead of JSON, the controller expects JSON DTO. If you want to support raw text, see the “Next steps” section.

2) Redirect to original URL
- Method: GET
- Path: `/{shortCode}`
- Example: If the service returned `http://short.url/abc123`, call `GET http://localhost:8080/abc123`.
- Behavior:
  - If the short code exists and is not expired: returns HTTP 302 Found with `Location` header set to the original URL.
  - If not found or expired: returns HTTP 404 (handled via `ResourceNotFoundException` and global exception handler).

Manual test of redirect using curl (show headers):
```bash
curl -i -X GET http://localhost:8080/abc123
```
Expected response headers:
```
HTTP/1.1 302 Found
Location: https://example.com/very/long/path
```

Running tests
```bash
./mvnw test
```

Troubleshooting
- If you see validation errors resolved by compilation but runtime validation exceptions occur, ensure `spring-boot-starter-validation` is present (it is in the provided `pom.xml`).
- If the app doesn't start:
  - Check logs printed to console when running `spring-boot:run`.
  - Ensure database config is correct. For quick local testing, configure an in-memory DB (H2) or ensure Postgres is running with the configured URL.
- If dependencies look missing, run:
  ```bash
  ./mvnw -U clean package
  ```
  to refresh and force-download dependencies.

Project file highlights
- Controller: `src/main/java/com/discretebody/urlshortner/controller/ShortUrlController.java`
  - POST /create — create short URL
  - GET /{shortCode} — redirect to original URL
- Service: `src/main/java/com/discretebody/urlshortner/services/ShortUrlService.java`
  - Main logic for generating short codes and saving `ShortUrl` entities
- Entity: `src/main/java/com/discretebody/urlshortner/entity/ShortUrl.java`
- Repository: `src/main/java/com/discretebody/urlshortner/repository/ShortUrlRepository.java`
- DTOs: `src/main/java/com/discretebody/urlshortner/dto/UrlShortRequestDto.java` and `UrlShortResponseDto.java`
- Utilities: `src/main/java/com/discretebody/urlshortner/util/ShortCodeGenerator.java`
- Exceptions & global handler: `src/main/java/com/discretebody/urlshortner/exception/*`

Next steps and enhancements
- Support plain text `text/plain` POST bodies for convenience (currently expects JSON DTO).
- De-duplicate URLs: return existing short URL when the same original URL already exists.
- Analytics: track click counts, referrers, timestamps.
- Rate-limiting & Abuse protection.
- Add integration tests for end-to-end behavior (create + redirect).
- Add Dockerfile + docker-compose for local DB and app setup.

License & contribution
- Add a LICENSE file if you plan to open-source this repo.
- Add a CONTRIBUTING.md with instructions for tests and code style if you expect external contributions.

If you'd like, I can also add a Postman collection, create a simple script that runs a few curl tests, or implement one of the next-step features — tell me which and I'll add it.

# URL Shortener

A high-performance Spring Boot service that creates short URLs and redirects them to the original URLs. Built with enterprise-grade caching and optimized for low-latency redirects.

## ✨ Features

- **URL Shortening**: Convert long URLs into compact 6-character short codes
- **Fast Redirects**: HTTP 302 redirects from short URLs to original URLs
- **Redis Caching**: Implements the Cache-Aside pattern for sub-millisecond response times
- **Sliding Expiration**: Frequently accessed URLs automatically refresh their Time-To-Live (TTL) in the cache, keeping popular links hot in memory
- **Negative Caching**: Protects the database from repeated lookups of non-existent short codes
- **URL Validation**: Validates URL format and optionally pings the destination
- **Graceful Fallback**: Continues working even if Redis is unavailable
- **30-Day Expiration**: Short URLs automatically expire after 30 days

## 🛠️ Tech Stack

- **Java 21** - Modern Java with latest features
- **Spring Boot 4.0.1** - Application framework
- **Spring Data JPA** - Database persistence layer
- **PostgreSQL** - Primary data store
- **Spring Data Redis** - High-performance caching layer
- **ModelMapper** - Object mapping
- **Lombok** - Reduces boilerplate code
- **Jakarta Validation** - Request validation
- **Maven** - Build and dependency management

## 📋 Prerequisites

Before running the application, ensure you have the following installed and running:

### Required
- Java 21 or higher
- PostgreSQL database
- Redis server (optional but recommended for optimal performance)

### Database Setup

**Option 1: Docker (Recommended)**

```bash
# Start PostgreSQL
docker run --name postgres \
  -e POSTGRES_DB=urlshortnerdb \
  -e POSTGRES_USER=postgres \
  -e POSTGRES_PASSWORD=postgres \
  -p 5432:5432 \
  -d postgres

# Start Redis
docker run --name redis \
  -p 6379:6379 \
  -d redis
```

**Option 2: Local Installation**

Install PostgreSQL and Redis locally, then create a database named `urlshortnerdb`.

## 🚀 Quick Start

### 1. Clone the Repository

```bash
git clone https://github.com/discreteBody/UrlShortner.git
cd UrlShortner
```

### 2. Configure Database Connection

Edit `src/main/resources/application.properties` if your database credentials differ:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/urlshortnerdb
spring.datasource.username=postgres
spring.datasource.password=postgres
```

### 3. Run the Application

**Windows (PowerShell):**
```powershell
.\mvnw.cmd spring-boot:run
```

**Linux/macOS:**
```bash
./mvnw spring-boot:run
```

The application will start on **http://localhost:9030**

## 📡 API Endpoints

### Create Short URL

Generates a short URL for a given original URL.

**Request:**
```bash
POST http://localhost:9030/create
Content-Type: application/json

{
  "originalUrl": "https://www.example.com/very/long/url/path"
}
```

**Response:**
```json
{
  "id": 1,
  "shortCode": "abc123",
  "shortUrl": "http://localhost:9030/abc123",
  "originalUrl": "https://www.example.com/very/long/url/path",
  "creationDate": "2026-01-17T20:28:56",
  "expirationDate": "2026-02-16T20:28:56"
}
```

**Example with curl:**
```bash
curl -X POST http://localhost:9030/create \
  -H "Content-Type: application/json" \
  -d '{"originalUrl":"https://www.example.com"}'
```

### Redirect to Original URL

Redirects to the original URL using the short code.

**Request:**
```bash
GET http://localhost:9030/{shortCode}
```

**Response:**
- **302 Found** - Redirects to the original URL
- **404 Not Found** - Short code doesn't exist

**Example:**
```bash
# Browser or curl will automatically follow the redirect
curl -L http://localhost:9030/abc123
```

### List All Short URLs

Returns all short URLs in the system.

**Request:**
```bash
GET http://localhost:9030/
```

**Response:**
```json
[
  {
    "id": 1,
    "shortCode": "abc123",
    "shortUrl": "http://localhost:9030/abc123",
    "originalUrl": "https://www.example.com",
    "creationDate": "2026-01-17T20:28:56",
    "expirationDate": "2026-02-16T20:28:56"
  }
]
```

## ⚙️ Configuration

The application is configured via `src/main/resources/application.properties`:

```properties
# Application
spring.application.name=UrlShortner
server.port=9030
app.base-url=http://localhost:9030/

# Database
spring.datasource.url=jdbc:postgresql://localhost:5432/urlshortnerdb
spring.datasource.username=postgres
spring.datasource.password=postgres
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true

# Redis Cache
spring.data.redis.host=localhost
spring.data.redis.port=6379
spring.data.redis.timeout=2000
```

### Configuration Options

| Property | Description | Default |
|----------|-------------|---------|
| `server.port` | Application server port | `9030` |
| `app.base-url` | Base URL for generated short URLs | `http://localhost:9030/` |
| `spring.data.redis.host` | Redis server host | `localhost` |
| `spring.data.redis.port` | Redis server port | `6379` |

## 🏗️ Architecture

### Caching Strategy

The application uses a **Cache-Aside** pattern with Redis:

1. **Cache Hit**: Returns cached URL immediately (sub-millisecond response)
2. **Cache Miss**: Queries PostgreSQL, then caches the result for 1 hour
3. **Sliding Expiration**: Each cache hit extends the TTL by 1 hour
4. **Negative Caching**: Non-existent codes are cached for 1 minute to protect the database

### Short Code Generation

- Uses `SecureRandom` for cryptographically strong random codes
- Character set: `a-z`, `A-Z`, `0-9` (62 characters)
- Length: 6 characters
- Total possible combinations: 62^6 = approximately 56.8 billion unique codes
- Collision detection: Regenerates if code already exists

## 📁 Project Structure

```
src/main/java/com/discretebody/urlshortner/
├── config/              # Application configuration
│   ├── ModelMapperConfig.java
│   └── RedisConfig.java
├── controller/          # REST API controllers
│   └── ShortUrlController.java
├── dto/                 # Data Transfer Objects
│   ├── UrlShortRequestDto.java
│   └── UrlShortResponseDto.java
├── entity/              # JPA entities
│   └── ShortUrl.java
├── exception/           # Exception handling
│   ├── ApiError.java
│   ├── BadRequestException.java
│   ├── GlobalExceptionHandler.java
│   └── ResourceNotFoundException.java
├── repository/          # Data access layer
│   └── ShortUrlRepository.java
├── services/            # Business logic
│   └── ShortUrlService.java
└── util/                # Utility classes
    ├── PingService.java
    ├── ShortCodeGenerator.java
    └── UrlValidator.java
```

## 🧪 Testing

Run tests with Maven:

```bash
./mvnw test
```

## 🔨 Building

Build the application:

```bash
./mvnw clean package
```

The executable JAR will be created in `target/UrlShortner-0.0.1-SNAPSHOT.jar`

Run the JAR:

```bash
java -jar target/UrlShortner-0.0.1-SNAPSHOT.jar
```

## 🐛 Troubleshooting

### PostgreSQL Connection Issues

- Ensure PostgreSQL is running: `docker ps` or check your local service
- Verify database exists: `psql -U postgres -c "\l"`
- Check credentials in `application.properties`

### Redis Connection Issues

- The application will work without Redis but with reduced performance
- Check Redis is running: `docker ps` or `redis-cli ping`
- Logs will show: "Redis unavailable, falling back to Postgres"

### Port Already in Use

Change the port in `application.properties`:
```properties
server.port=8080
```

## 🚧 Roadmap

Future enhancements planned:

- [ ] Spring Security integration for API authentication
- [ ] Web UI (React/Thymeleaf) for URL management
- [ ] Custom short codes (user-defined aliases)
- [ ] Click analytics and tracking
- [ ] QR code generation for short URLs
- [ ] Bulk URL shortening
- [ ] API rate limiting

## 📝 License

This project is available for use under standard open source terms.

## 👤 Author

**discreteBody**

## 🤝 Contributing

Contributions, issues, and feature requests are welcome!

# 🔗 URL Shortener: High-Performance Redirect Service


A high-concurrency Spring Boot service optimized for low-latency URL redirection using enterprise-grade caching strategies.
## 🛠️ Tech Stack

🛠️ Tech Stack
Backend: Java 21, Spring Boot 4.0.1, Spring Data JPA

Database: PostgreSQL (Primary Store)

Caching: Redis (High-speed lookups)

Tools: Maven, Docker, Lombok, ModelMapper

## ⚡ Key Technical Features

1. Advanced Caching StrategyImplements a Cache-Aside pattern to minimize database hits:Performance: Sub-millisecond redirects for cached URLs.Sliding Expiration: Cache TTL (1 hour) automatically resets on every hit, keeping popular links in memory.Negative Caching: Non-existent keys are cached for 1 minute to prevent Cache Penetration attacks on the database.Resilience: Graceful fallback to PostgreSQL if Redis is unavailable.
2. Optimized Short-Code GenerationUses SecureRandom for cryptographically strong 6-character alphanumeric codes.Supports 56.8 Billion unique combinations ($62^6$).Includes built-in collision detection and automatic regeneration logic.
3. Clean ArchitectureValidation: Jakarta Validation for URL format and accessibility pings.Data Flow: Strict separation between Entities and DTOs using ModelMapper.Error Handling: Global Exception Handler for consistent REST API responses.

## 📋 Prerequisites

Before running the application, ensure you have the following installed and running:



### API Reference

## Create Short URL

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


## 🚧 Roadmap

Future enhancements planned:

- [ ] Spring Security integration for API authentication
- [ ] Web UI (React/Thymeleaf) for URL management
- [ ] Click analytics and tracking
- [ ] QR code generation for short URLs
- [ ] API rate limiting


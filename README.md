Here is the updated README.md. I have integrated the Redis instructions, the Sliding Expiration logic we discussed, and the necessary Docker commands to get Redis running.

You can copy and paste this directly into your repository.

UrlShortner
A high-performance Spring Boot service that creates short URLs and redirects short codes to the original URLs.

New Features:

Redis Caching: Implements the Cache-Aside pattern for sub-millisecond response times.

Sliding Expiration: Frequently accessed URLs automatically refresh their Time-To-Live (TTL) in the cache, keeping popular links hot in memory.

Tech Stack
Java 21

Spring Boot 4.x

Spring Data JPA (PostgreSQL)

Spring Data Redis (Caching)

ModelMapper

Lombok

Docker (For local Redis instance)

Quick Start (Development)
1. Prerequisites (Database & Cache)
Before running the app, ensure PostgreSQL and Redis are running.

Start Redis using Docker:

Bash

docker run --name redis -p 6379:6379 -d redis
Start PostgreSQL (if using Docker):

Bash

docker run --name postgres -e POSTGRES_PASSWORD=yourpassword -p 5432:5432 -d postgres
2. Clone and Run
Clone the repo:

Bash

git clone <your-repo-url>
From project root, run with the included Maven wrapper:

PowerShell / Windows:

PowerShell

.\mvnw.cmd spring-boot:run
Bash / macOS / Linux:

Bash

./mvnw spring-boot:run
Application will start on port 8080 by default.

Configuration
The service reads configuration from src/main/resources/application.properties.




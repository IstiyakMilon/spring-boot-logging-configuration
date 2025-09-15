# spring-boot-logging-configuration

A Spring Boot service exposing Posts and Comments APIs backed by the external JSONPlaceholder API via Spring WebClient. It demonstrates structured logging with correlation IDs and per-level file appenders.

## Tech stack
- Java 17+
- Spring Boot 3
- Spring WebFlux (WebClient)
- Spring Web MVC
- Custom Page/Pageable for pagination (no Spring Data dependency)
- springdoc-openapi (Swagger UI)
- Maven
- H2 in-memory database (for local persistence)


## Prerequisites
- Java 17+ installed (check with: `java -version`)
- Maven 3.9+ installed (check with: `mvn -version`)
- Internet access (the app calls https://jsonplaceholder.typicode.com)


## Configuration
Default application properties are in `src/main/resources/application.properties`.

Key settings:
- Server port: `server.port=8080` (base URL will be `http://localhost:8080`)
- Logging file targets (relative paths):
  - `logging.file.debug.path=logs/debug.log`
  - `logging.file.info.path=logs/info.log`
  - `logging.file.warn.path=logs/warn.log`
  - `logging.file.error.path=logs/error.log`
- H2 database: In-memory, no setup required

Notes:
- External API base URL is fixed to `https://jsonplaceholder.typicode.com` in the services.
- All controllers expect the header `X-Correlation-Id` per request and log it for tracing.


## Build and run
- Build the project:
```bash
mvn clean package
```

- Run in dev mode:
```bash
mvn spring-boot:run
```

- Or run the packaged jar:
```bash
java -jar target/spring-boot-logging-configuration-0.0.1-SNAPSHOT.jar
```

App will start at:
- http://localhost:8080


## Correlation ID
All endpoints expect a request header:
- `X-Correlation-Id: <any-unique-string>`

The value is logged and propagated downstream to external calls.


## API overview
Base URL: `http://localhost:8080`

DTOs:
- PostDto: `{ "userId": number, "id": number, "title": string, "body": string }`
- CommentDto: `{ "postId": number, "id": number, "name": string, "email": string, "body": string }`


### 1) Get paged posts
GET `/posts`

Query params (optional):
- `page` default `0`
- `size` default `10`
- `sortBy` default `id` (supported: `id`, `title`, `userId`)
- `direction` default `asc` (values: `asc|desc`)

Examples:
```bash
# Default pagination (page 0, size 10, sort=id asc)
curl -X GET "http://localhost:8080/posts" \
  -H "X-Correlation-Id: demo-correlation-123" \
  -H "Accept: application/json"

# Custom pagination and sorting
curl -X GET "http://localhost:8080/posts?page=0&size=5&sortBy=title&direction=desc" \
  -H "X-Correlation-Id: demo-correlation-123" \
  -H "Accept: application/json"
```

Sample response (truncated):
```json
{
  "content": [
    { "userId": 1, "id": 1, "title": "Post title", "body": "Post body text" }
  ],
  "pageable": { "pageNumber": 0, "pageSize": 5, "sort": { "sorted": true } },
  "totalPages": 20,
  "totalElements": 100,
  "size": 5,
  "number": 0,
  "first": true,
  "last": false,
  "numberOfElements": 5,
  "empty": false
}
```


### 2) Get a single post by id
GET `/posts/{id}`
```bash
curl -X GET "http://localhost:8080/posts/1" \
  -H "X-Correlation-Id: demo-correlation-123" \
  -H "Accept: application/json"
```


### 3) Get comments for a post
GET `/posts/{id}/comments`
```bash
curl -X GET "http://localhost:8080/posts/1/comments" \
  -H "X-Correlation-Id: demo-correlation-123" \
  -H "Accept: application/json"
```


### 4) Create a post
POST `/posts`
```bash
curl -X POST "http://localhost:8080/posts" \
  -H "X-Correlation-Id: demo-correlation-123" \
  -H "Content-Type: application/json" \
  -H "Accept: application/json" \
  -d '{
    "userId": 1,
    "title": "New Post Title",
    "body": "This is the content of the new post."
  }'
```


### 5) Update a post (full)
PUT `/posts/{id}`
```bash
curl -X PUT "http://localhost:8080/posts/1" \
  -H "X-Correlation-Id: demo-correlation-123" \
  -H "Content-Type: application/json" \
  -H "Accept: application/json" \
  -d '{
    "userId": 1,
    "title": "Updated Post Title",
    "body": "This is the updated content of the post."
  }'
```


### 6) Update a post (partial)
PATCH `/posts/{id}`
```bash
curl -X PATCH "http://localhost:8080/posts/1" \
  -H "X-Correlation-Id: demo-correlation-123" \
  -H "Content-Type: application/json" \
  -H "Accept: application/json" \
  -d '{
    "title": "Partially Updated Title"
  }'
```


### 7) Delete a post
DELETE `/posts/{id}`
```bash
curl -X DELETE "http://localhost:8080/posts/1" \
  -H "X-Correlation-Id: demo-correlation-123"
```


### 8) Get comments (optionally by postId)
GET `/comments?postId={id}`
```bash
# All comments
curl -X GET "http://localhost:8080/comments" \
  -H "X-Correlation-Id: demo-correlation-123" \
  -H "Accept: application/json"

# Comments for a specific post
curl -X GET "http://localhost:8080/comments?postId=1" \
  -H "X-Correlation-Id: demo-correlation-123" \
  -H "Accept: application/json"
```


## Swagger / OpenAPI
Swagger UI is enabled via springdoc.

- Swagger UI: http://localhost:8080/swagger-ui/index.html
- OpenAPI JSON: http://localhost:8080/v3/api-docs

In the UI, set the header `X-Correlation-Id` when trying requests.


## Logging
Logback is configured (see `src/main/resources/logback-spring.xml`) to write per-level logs to files. Paths are configured in `application.properties` as relative paths. All logs are written to the `logs/` directory (see `.gitignore`).


## Troubleshooting
- 400 Bad Request: Ensure you send `X-Correlation-Id` header; controllers mark it as required.
- 5xx from external API: JSONPlaceholder may be unavailable; retry later.
- Connection issues: Confirm internet access and that a proxy/firewall isn’t blocking outbound HTTP.
- Port conflicts: Change `server.port` in `application.properties`.


## Development notes
- Services (`PostService`, `CommentService`) call JSONPlaceholder using `WebClient`.
- Pagination is applied in-service on the fetched list and returned as a custom `PageResponse` object (not Spring Data).
- OpenAPI is customized to add the `X-Correlation-Id` header parameter to all operations.
- H2 in-memory database is used for local persistence and testing.

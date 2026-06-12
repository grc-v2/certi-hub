# certi-master

Backend Spring Boot Modulith của nền tảng CertiMaster. REST API cho frontend [`certi-client`](../certi-client).

> Tổng quan kiến trúc, danh sách tính năng, mô tả module và API chi tiết xem ở [README tổng](../README.md).

---

## Tech stack

| Layer | Công nghệ |
|---|---|
| Runtime | Java 21, Spring Boot 4.0.6 |
| Modularity | Spring Modulith 2.0.6 (4 module: `identity`, `learning`, `engagement`, `lookup` + `shared`) |
| Web | Spring Web MVC, Validation, RestClient |
| Persistence | Spring Data JPA, Hibernate, PostgreSQL, Flyway |
| Cache / Session | Spring Data Redis |
| Messaging | Spring Kafka + Spring Modulith Events (Kafka) |
| Security | Spring Security 6, OAuth2 Resource Server, JJWT 0.12.3 (RSA), BouncyCastle |
| Storage | MinIO (avatar bucket) |
| Observability | Actuator, Micrometer Prometheus, OpenTelemetry (logs/metrics/traces) |
| Testing | JUnit 5, Spring Modulith Test, Testcontainers |

---

## Quickstart

### Yêu cầu

- JDK 21
- PostgreSQL 15+ chạy tại `localhost:5433`, database `certimaster`
- Redis 7+ tại `localhost:6379` (password `redis123` mặc định)
- Kafka tại `localhost:29092` (cần cho event publishing)
- MinIO tại `localhost:9000` (cần cho avatar upload)

> Cách dựng nhanh 4 dịch vụ trên bằng Docker Compose xem [docs/setup.md](../docs/setup.md).

### Chạy local

```bash
# 1) Tạo database
createdb -h localhost -p 5433 -U postgres certimaster

# 2) Chạy migration + start app (profile dev)
./mvnw spring-boot:run -Dspring-boot.run.profiles=dev

# 3) Kiểm tra
curl http://localhost:8080/actuator/health
```

App chạy ở `http://localhost:8080`. Tất cả API dưới prefix `/api/v1`.

### Lệnh thường dùng

```bash
./mvnw clean compile           # biên dịch
./mvnw test                    # chạy test (cần Docker cho Testcontainers)
./mvnw spring-boot:run         # chạy app
./mvnw package -DskipTests     # build JAR
docker build -t certi-master . # build image
```

---

## Cấu trúc module

```
src/main/java/com/certimaster/
├── identity/      # User, Role, Auth (JWT, OAuth2), Session, Email verification
├── learning/      # Certification, Domain, Exam, Question, ExamSession, Course, Tag
├── engagement/    # Community (Post, Comment, Vote, Bookmark), Notification
├── lookup/        # Reference data lookup APIs
└── shared/        # Security, exception, advice, validation, constants, base entity
```

Mỗi module có cấu trúc Hexagonal: `adapter/{web,persistence}` ↔ `application/{command,query}` ↔ `domain/{entity,service,event}`.

---

## Migration

Flyway scripts tại `src/main/resources/db/migration/`:

| File | Module |
|---|---|
| `V1__identity_schema.sql` | identity |
| `V2__learning_schema.sql` | learning |
| `V3__engagement_schema.sql` | engagement |
| `V4__course_schema.sql` | course (within learning) |

Bật Flyway bằng `FLYWAY_ENABLED=true` (mặc định `false` để tránh accident).

---

## Cấu hình môi trường

Tất cả config qua env var, fallback xem trong [`application.yml`](src/main/resources/application.yml). Các nhóm chính:

| Prefix | Mục đích |
|---|---|
| `DB_*` | PostgreSQL connection + Hikari pool |
| `REDIS_*` | Redis host/port/password |
| `KAFKA_*` | Kafka bootstrap server |
| `JWT_*` | RSA keypair, token TTL, issuer |
| `AUTH_OAUTH2_*` | Google/GitHub OAuth client |
| `AUTH_COOKIE_*` | Cookie security flags |
| `MINIO_*` | MinIO endpoint + credentials |
| `EXAM_SESSION_*` | Max concurrent session, auto-expire |
| `CORS_*` | CORS allowed origins/methods/headers |
| `OTEL_*` / `TRACING_*` | OpenTelemetry export |

> Danh sách env var đầy đủ + giá trị mẫu xem [docs/setup.md](../docs/setup.md).

⚠️ **`application.yml` hiện chứa secret mặc định cho dev** (JWT key, OAuth client, MinIO). Không dùng giá trị mặc định cho production — phải set env var override. Xem [docs/setup.md#bảo-mật-secrets](../docs/setup.md#bảo-mật-secrets).

---

## API documentation

Sau khi bật `springdoc-openapi` (xem [#A4 trong kế hoạch tài liệu](../README.md)):

- Swagger UI: <http://localhost:8080/swagger-ui.html>
- OpenAPI JSON: <http://localhost:8080/v3/api-docs>

Tất cả Swagger endpoint đã được permit trong `SecurityConfig`.

---

## Testing

```bash
./mvnw test                                                    # toàn bộ
./mvnw test -Dtest=LearningModuleIntegrationTest               # 1 file
./mvnw test -Dtest='*ModuleIntegrationTest'                    # tất cả module integration
```

Module test dùng `@ApplicationModuleTest` của Spring Modulith — verify boundary giữa các module. `ModularityTests` verify cyclic dependency.

Test integration cần Docker chạy để dựng Testcontainers (Postgres).

---

## Observability

| Endpoint | Mục đích |
|---|---|
| `GET /actuator/health` | Health check (Kubernetes probe ready) |
| `GET /actuator/info` | Build info |
| `GET /actuator/metrics` | Micrometer metrics |
| `GET /actuator/prometheus` | Prometheus scrape endpoint |
| `GET /actuator/modulith` | Spring Modulith module graph |

OpenTelemetry export tắt mặc định (`OTEL_*_EXPORT_ENABLED=false`). Bật khi cần ship logs/metrics/traces tới collector.

---

## Tài liệu liên quan

- [README tổng](../README.md) — kiến trúc, API chi tiết, frontend
- [docs/setup.md](../docs/setup.md) — setup chi tiết, env vars, troubleshooting
- [docs/glossary.md](../docs/glossary.md) — định nghĩa thuật ngữ nghiệp vụ

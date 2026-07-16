# certi-master

Backend Spring Boot Modulith của nền tảng CertiMaster. REST API cho frontend [`certi-client`](../certi-client).

> Tổng quan kiến trúc, danh sách tính năng, mô tả module và API chi tiết xem ở [README tổng](../README.md).

---

## Tech stack

| Layer | Công nghệ |
|---|---|
| Runtime | Java 21, Spring Boot 4.0.6 |
| Modularity | Spring Modulith 2.0.6 (4 module: `identity`, `learning`, `engagement`, `productivity` + `shared`) |
| Web | Spring Web MVC, Validation, RestClient |
| Persistence | Spring Data JPA, Hibernate, PostgreSQL, Flyway |
| Cache / Session | Spring Data Redis |
| Messaging | Spring Modulith Events (in-process `ApplicationEvent`, không dùng broker ngoài) |
| Mail | Spring Boot Mail (SMTP; dev dùng Mailpit) |
| Security | Spring Security 6, OAuth2 Resource Server, JJWT 0.12.3 (RSA), BouncyCastle |
| Storage | MinIO (avatar + resources bucket) |
| Observability | Actuator, Micrometer Prometheus, OpenTelemetry (logs/metrics/traces) |
| Testing | JUnit 5, Spring Modulith Test, Testcontainers |

---

## Quickstart

### Yêu cầu

- JDK 21
- PostgreSQL 17 chạy tại `localhost:5433`, database `certimaster`
- Redis 7+ tại `localhost:6379` (password `redis123` mặc định)
- MinIO tại `localhost:9000` (cần cho avatar + resource upload)
- SMTP tại `localhost:1025` — dev dùng Mailpit (UI `localhost:8025`); mail có thể tắt bằng `APP_MAIL_ENABLED=false`

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
├── identity/      # User, Role, Auth (JWT, OAuth2), Session, Email verification, mail adapter
├── learning/      # Certification, Domain, Exam, Question, UserExamSession, ExamResult, Tag
├── engagement/    # Blog (Post, Comment, Bookmark), Notification
├── productivity/  # Todo, Pomodoro (settings + sessions), Habit, TrackerResource
└── shared/        # Security, exception, advice, validation, constants, base entity
```

Mỗi module có cấu trúc Hexagonal: `adapter/{web,persistence}` ↔ `application/{command,query}` ↔ `domain/{entity,service,event}`. `learning` expose named interface `lookup` cho `productivity` tra cứu certification (xem `package-info.java`).

---

## Migration

Flyway scripts tại `src/main/resources/db/migration/`:

| File | Nội dung |
|---|---|
| `V1__identity_schema.sql` | identity (users, roles, tokens) + modulith `event_publication` |
| `V2__learning_schema.sql` | learning (certification, exam, question, session, result) |
| `V3__engagement_schema.sql` | engagement (post, tag, bookmark) |
| `V4`–`V6` | course schema tạo rồi drop (LMS đã gỡ — xem ADR-0004) |
| `V7`–`V9` | notification/comment, resource hub, habit |
| `V10__pomodoro_sessions.sql` | pomodoro session history |
| `V11__tracker_resource_certification.sql` | gắn resource vào certification |

Flyway bật mặc định (`FLYWAY_ENABLED=true`); toàn bộ schema tái tạo được từ V1–V11. DB cũ chưa có `flyway_schema_history` sẽ baseline ở version 1 rồi chạy V2+ (idempotent). `SchemaMigrationIntegrationTest` verify Hibernate `validate` khớp toàn bộ entity.

---

## Cấu hình môi trường

Tất cả config qua env var, fallback xem trong [`application.yml`](src/main/resources/application.yml). Các nhóm chính:

| Prefix | Mục đích |
|---|---|
| `DB_*` | PostgreSQL connection + Hikari pool |
| `REDIS_*` | Redis host/port/password |
| `JWT_*` | RSA keypair (bắt buộc — app fail-fast nếu thiếu), token TTL, issuer |
| `AUTH_OAUTH2_*` | Google/GitHub OAuth client |
| `AUTH_COOKIE_*` | Cookie security flags |
| `MAIL_*` / `APP_MAIL_*` | SMTP host/port, from, frontend base URL, enable flag |
| `MINIO_*` | MinIO endpoint + credentials |
| `EXAM_SESSION_*` | Max concurrent session, auto-expire |
| `CORS_*` | CORS allowed origins/methods/headers |
| `OTEL_*` / `TRACING_*` | OpenTelemetry export |

> Danh sách env var đầy đủ + giá trị mẫu xem [deploy/.env.example](../deploy/.env.example) và [docs/setup.md](../docs/setup.md).

✅ **`application.yml` không còn chứa secret thật.** JWT keypair bắt buộc set qua env (app fail-fast nếu thiếu); profile `dev` có thể tự sinh keypair tạm bằng `jwt.generate-dev-keys=true`. OAuth client id/secret mặc định rỗng — chỉ endpoint OAuth báo lỗi khi chưa cấu hình, các luồng còn lại vẫn chạy. Cách sinh keypair dev xem [docs/setup.md](../docs/setup.md).

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

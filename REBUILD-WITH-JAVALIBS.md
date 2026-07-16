# Kế hoạch triển khai lại certi-master trên nền javalibs

Hướng dẫn chi tiết, từng bước để **dựng lại** backend `certi-master`:

- Chia lại domain cho **clean & simple** (giữ Spring Modulith, đánh giá lại 4 bounded context).
- Tích hợp thư viện dùng chung **javalibs** (`io.javalibs`, Spring Boot 3.5.3).
- Áp **cấu trúc package mới** tối ưu cho monolith cá nhân + javalibs.
- Dùng javalibs **hiệu quả**: config-first, `config/` rỗng, không viết lại thứ thư viện đã lo.

> Tài liệu này là **kế hoạch triển khai**, không phải README vận hành. README hiện tại (`README.md`) mô tả trạng thái cũ (Boot 4.0.6). Sau khi hoàn tất theo kế hoạch này, cập nhật lại `README.md`.

---

## 0. Ba quyết định kiến trúc nền tảng (đã chốt)

| Quyết định | Lựa chọn | Hệ quả |
|---|---|---|
| **Phiên bản Boot** | Hạ certi-master **4.0.6 → 3.5.3** để khớp javalibs | Dùng được toàn bộ starter javalibs ngay; ít rủi ro nhất. Xem [§6](#6-pomxml-mới--hạ-boot--khai-báo-javalibs). |
| **Kiến trúc triển khai** | **Giữ Spring Modulith** (1 monolith, nhiều bounded context) | Vẫn 1 deployable, boundary được enforce bằng `ModularityTests`. javalibs lo phần khung hạ tầng. |
| **Mức phân lớp trong mỗi context** | **Mức A — DDD + CQRS đầy đủ** | `api → application(command/query) → domain(model/event/rule) → infrastructure`. Dùng `javalibs-ddd` + `javalibs-cqrs`. |

> **Vì sao hạ Boot chứ không nâng javalibs?** javalibs build trên Boot 3.5.3 / Spring Framework 6. Auto-configuration của nó đăng ký qua `META-INF/spring/...AutoConfiguration.imports` và giả định API của Framework 6 (servlet, security DSL, Flyway customizer...). Boot 4.0.6 kéo theo Spring Framework 7 với nhiều breaking change → bean của javalibs có thể fail khi khởi động. Với một bản **dựng mới**, hạ certi-master về 3.5.3 là con đường thẳng; nâng javalibs lên Boot 4 là một dự án riêng, để sau.

---

## 1. Nguyên tắc xuất phát (đọc trước khi code)

1. **javalibs lo phần khung — service chỉ còn nghiệp vụ.** Nếu bạn đang viết `GlobalExceptionHandler`, JWT filter, CORS filter, correlation-id filter, chuẩn JSON lỗi, `PageResponse`, cấu hình Flyway an toàn, hay Swagger config → **dừng lại**, javalibs đã có. Xem bảng thay thế [§4](#4-bản-đồ-thay-thế-shared--javalibs).
2. **Phân lớp bằng package, không bằng Maven module.** certi-master vẫn là **một** Maven module. Đừng bắt chước cấu trúc 4 tầng `core→spring→autoconfigure→starter` của javalibs — đó là kiến trúc *của thư viện* để client chọn lọc dependency.
3. **`config/` càng rỗng càng tốt.** Mọi tùy biến javalibs đi qua: (1) property `javalibs.*` trong yaml, (2) nếu không đủ thì khai một `@Bean` cùng type — javalibs tự lùi nhờ `@ConditionalOnMissingBean`.
4. **Không tạo `shared/util` vội.** Tiện ích chung đã có `javalibs-internal` (`Strings`, `Checks`, `Dates`, `Ids`).
5. **`domain` thuần Java.** Không Spring, không JPA*, không Jackson trong `domain/model` → unit test domain chạy bằng mili-giây. (\*ngoại lệ thực dụng về JPA: xem [§5.4](#54-ghi-chú-thực-dụng-jpa--domain).)

---

## 2. Đánh giá lại 4 bounded context (theo yêu cầu)

Bốn context hiện tại: `identity`, `learning`, `engagement`, `productivity` + `shared`. Đánh giá:

### 2.1 Vấn đề phát hiện

| # | Quan sát | Ảnh hưởng |
|---|---|---|
| P1 | **`engagement` đang gánh 2 subdomain khác nhau**: *Blog/Content* (Post, Comment, Bookmark, Tag) và *Notification* (Notification + 3 listener: `ExamActivityRecorder` ← learning, `HabitReminderNotifier` ← productivity, `BroadcastNotificationCreator`). | Notification là **supporting subdomain xuyên suốt**, không thuộc về "engagement/blog". Nó phụ thuộc ngược vào learning & productivity → module phình, coupling cao. |
| P2 | **Trùng khái niệm "Tag"**: `learning` có `Tag`; `engagement` có `PostTag`/`PostTagMapping`. | Hai mô hình tag rời rạc, dễ nhầm. Cần tách bạch rõ *tag câu hỏi/chứng chỉ* vs *tag bài viết*. |
| P3 | **`productivity` tham chiếu chéo `learning`** (V11: `tracker_resource` gắn `certification`), dùng named interface `lookup`. | Chấp nhận được, nhưng phải đi qua **named interface** của Modulith, không import entity trực tiếp. |
| P4 | **`shared` đang là "everything bucket"**: security, exception, advice, dto, validation, util, event, constant, config. | Phần lớn nội dung `shared/` sẽ **biến mất** khi thay bằng javalibs (xem §4). Phần còn lại rất mỏng. |

### 2.2 Đề xuất chia lại (clean & simple)

**Đề xuất chính: tách `notification` thành context riêng, đổi tên `engagement` → `content`.**

```
identity        — User, Role, Auth (issue token), Session, Email verification
learning        — Certification, Domain, Exam, Question, UserExamSession, ExamResult, QuestionTag
content          (đổi từ engagement) — Post, Comment, Bookmark, PostTag  ← chỉ còn blog/content
notification     (TÁCH RA)  — Notification + toàn bộ listener xuyên module
productivity    — Todo, Pomodoro, Habit, TrackerResource/Collection
shared          — teo lại còn "shared kernel" tối thiểu (xem §4.3)
```

**Lý do:**
- `notification` là nơi **duy nhất** được phép lắng nghe event từ mọi context khác (learning, productivity, content). Tách ra biến quan hệ "nhiều context → engagement" lộn xộn thành "mọi context phát event → notification tiêu thụ" — một chiều, sạch.
- `content` sau khi bỏ notification chỉ còn đúng một chủ đề: xuất bản nội dung. Dễ đọc, dễ test.
- Tag được tách theo ngữ cảnh: `learning.QuestionTag` vs `content.PostTag` — không còn mơ hồ.

**Nếu muốn tối giản hơn nữa (giữ 4 module):** giữ nguyên `engagement` nhưng **cô lập notification vào sub-package `engagement/notification/`** với ranh giới rõ, và bắt buộc mọi giao tiếp xuyên module đi qua **Spring Modulith events** (không gọi service trực tiếp). Kém sạch hơn P1 nhưng ít xáo trộn.

> **Khuyến nghị:** tách `notification` (5 context). Chi phí thấp, lợi ích rõ. Phần còn lại của tài liệu này dùng 5 context; nếu bạn giữ 4, chỉ cần đọc `content`+`notification` gộp làm `engagement`.

### 2.3 Quy tắc phụ thuộc giữa các context (Modulith enforce)

```
        ┌─────────────► identity (ai cũng đọc UserContext, nhưng KHÔNG import entity User)
        │
learning ──event──►┐
productivity ─event►├──► notification
content ──event───►┘
        │
productivity ──lookup(named interface)──► learning
content ──lookup──► learning (nếu cần domain/certification)
```

- **Giao tiếp xuyên context = Spring Modulith event** (`@ApplicationModuleListener`), **không** gọi service của module khác.
- Khi buộc phải tra cứu đồng bộ (vd productivity cần tên certification): expose **named interface** `lookup` ở learning (đã có tiền lệ), trả về DTO — không lộ entity.
- Danh tính người dùng: mọi context lấy `UserContext` từ **javalibs-security** (`@CurrentUser` / `UserContextHolder`), **không** phụ thuộc entity `User` của identity.

---

## 3. Kiến trúc tổng thể sau khi dựng lại

```
┌──────────────────────────────────────────────────────────────────┐
│  javalibs (io.javalibs.*)  — auto-config, KHÔNG là modulith module │
│  web · security · openapi · observability · persistence ·          │
│  search · ddd · cqrs · cache · internal · test                     │
└──────────────────────────────────────────────────────────────────┘
                    ▲ beans hạ tầng dùng chung cho mọi context
                    │
┌───────────────────┴──────────────────────────────────────────────┐
│  com.certimaster  (1 Spring Boot app / 1 Maven module)             │
│                                                                    │
│  identity   learning   content   notification   productivity      │
│  └ mỗi context: api → application → domain → infrastructure        │
│                                                                    │
│  shared (kernel tối thiểu: chỉ những gì javalibs KHÔNG cung cấp)   │
└────────────────────────────────────────────────────────────────────┘
```

---

## 4. Bản đồ thay thế: `shared/` → javalibs

Đây là phần **giá trị nhất** của việc tích hợp: hầu hết `shared/` bị xoá.

### 4.1 Xoá — thay bằng javalibs

| File/khái niệm hiện tại trong `shared/` | Thay bằng (javalibs) | Ghi chú |
|---|---|---|
| `advice/GlobalExceptionHandler` (530 dòng) | `javalibs-web` `GlobalExceptionHandler` (auto) | **Xoá toàn bộ.** javalibs tự đăng ký, xuất JSON lỗi chuẩn. |
| `dto/ApiResponse`, `ApiResponseMetadata`, `BaseResponse` | `io.javalibs.web.ApiResponse<T>` (`ok`/`error`) | Đổi import. |
| `dto/PageResponse` | `io.javalibs.web.PageResponse<T>` (`PageResponse.of(...)`, `.map(...)`) | Đổi import. |
| `exception/BaseException`, `BusinessException`, `ResourceNotFoundException`, `ForbiddenException`, `UnauthorizedException`, `InvalidTokenException` | `io.javalibs.web.exception.*`: `ApiException`, `BusinessException`, `ResourceNotFoundException(resource,id)`, `ConflictException`; `io.javalibs.security.InvalidTokenException` | Bỏ cây exception tự chế. Mã lỗi nghiệp vụ → `BusinessErrorCode` (§8). |
| `security/*` (SecurityConfig, JwtAuthenticationFilter, JwtUserPrincipal, SecurityUtils, JwtProperties **phần validate**) | `javalibs-security`: `JwtAuthenticationFilter`, `SecurityFilterChain`, `@CurrentUser`, `@RequireRole`, `UserContext`, `UserContextHolder` (auto) | **Chỉ phần *validate/filter/principal* bị thay.** Phần *issue token* (login) ở lại identity — xem §7. |
| `advice/ApiResponseBodyAdvice` | (bỏ) — trả `ApiResponse.ok(...)` tường minh trong controller | Envelope tường minh dễ đọc, hợp với OpenAPI. |
| Correlation-id thủ công (MDC `correlationId` trong GlobalExceptionHandler) | `javalibs-observability` `CorrelationIdFilter` (auto) | Header `X-Correlation-Id`, tự gắn MDC. |
| `config/OpenApiConfig` | `javalibs-openapi` (auto) | Bearer scheme + global error responses tự có. |
| `util/UlidUtil` (sinh id) | `io.javalibs.internal.Ids` (`uuid`, `compactUuid`, `shortId`) | Nếu bắt buộc ULID, giữ lại (javalibs chưa có ULID). |
| Flyway config an toàn (clean-disabled...) | `javalibs-persistence` (auto) | Chỉ giữ file migration. |
| CORS thủ công | `javalibs-web` CORS (`javalibs.web.cors.*`) | Bật qua yaml. |

### 4.2 Giữ nguyên — javalibs KHÔNG cung cấp (thuộc nghiệp vụ / hạ tầng riêng)

| Khái niệm | Ở đâu sau khi dựng lại |
|---|---|
| **Issue JWT** (login, RSA sign, refresh token, `JwtProvider`) | `identity/infrastructure/security` (javalibs chỉ *validate*, không *issue*). |
| **OAuth2 login** (Google/GitHub) | `identity` (adapter). |
| **Email/SMTP** (verification, mail adapter) | `identity/infrastructure/mail` + `notification` (không có module mail trong javalibs). |
| **MinIO** (avatar, resource upload) | `<context>/infrastructure/storage` (không có module storage). |
| **Markdown/flexmark + HTML sanitizer** | `content/domain/service` (nghiệp vụ nội dung). |
| **Habit streak, Pomodoro, ResourceKindDetector...** | `productivity/domain` (nghiệp vụ thuần). |
| `validation/ValidDateRange...` (custom validator) | `shared/validation` (kernel) — hoặc chuyển vào context dùng nó. |

### 4.3 `shared/` teo lại còn gì

Sau khi thay, `shared` chỉ nên còn **shared kernel** thực sự dùng chung mà javalibs không có:

```
shared/
├── validation/        # ValidDateRange, DateRangeValidator... (nếu >1 context dùng)
├── constant/          # Status enum dùng chung nhiều context
└── (KHÔNG còn) dto/, advice/, exception/, security/, config/, util/
```

Nếu một thứ chỉ 1 context dùng → **chuyển vào context đó**, đừng để ở shared.

---

## 5. Cấu trúc package mới (Mức A — DDD + CQRS)

### 5.1 Cây thư mục một context mẫu (`learning`)

```
src/main/java/com/certimaster/learning/
├── package-info.java                  # @ApplicationModule(allowedDependencies = {...})
│
├── api/                               # ── Trình diện (REST). KHÔNG chứa nghiệp vụ
│   ├── CertificationController.java
│   ├── ExamController.java
│   ├── dto/                           #    request/response record + mapper from()
│   │   ├── CreateExamRequest.java
│   │   └── ExamResponse.java
│   └── LearningErrors.java            #    catalog BusinessErrorCode ERR-LEARNING-xxx
│
├── application/                       # ── Use case (CQRS). Nơi DUY NHẤT mở @Transactional
│   ├── command/
│   │   ├── SubmitExamCommand.java     #    record ... implements Command<ExamResultId>
│   │   └── SubmitExamHandler.java     #    @Component implements CommandHandler<...>
│   ├── query/
│   │   ├── GetExamQuery.java          #    record ... implements Query<ExamResponse>
│   │   └── GetExamHandler.java
│   └── event/
│       └── ExamSubmittedListener.java #    @ApplicationModuleListener (nếu cần)
│
├── domain/                            # ── Nghiệp vụ THUẦN JAVA (0 Spring, 0 Jackson)
│   ├── model/
│   │   ├── Certification.java         #    extends AggregateRoot<UUID>
│   │   ├── Exam.java
│   │   ├── Question.java              #    Entity con
│   │   ├── ExamStatus.java            #    enum
│   │   └── Score.java                 #    ValueObject (record)
│   ├── event/
│   │   └── ExamSubmittedEvent.java    #    extends AbstractDomainEvent
│   ├── rule/
│   │   └── ExamMustHaveQuestions.java #    implements BusinessRule
│   └── CertificationRepository.java   #    PORT (interface)
│
├── application/lookup/                # ── Named interface cho context khác tra cứu
│   ├── package-info.java              #    @NamedInterface("lookup")
│   └── CertificationLookup.java       #    trả DTO, KHÔNG lộ entity
│
├── infrastructure/                    # ── Adapter kỹ thuật. Nơi DUY NHẤT biết JPA/MinIO
│   ├── persistence/
│   │   └── JpaCertificationRepository.java
│   └── storage/
│       └── MinioResourceStorage.java
│
└── config/                            # ── Ghi đè bean javalibs khi cần (lý tưởng: rỗng)
```

### 5.2 Chiều phụ thuộc package (bất biến)

```
api ─────►  application  ─────►  domain
                 ▲                  ▲
                 └── infrastructure ┘   (implement port của domain)
```

- `domain` không import gì ngoài JDK + `javalibs-ddd-core` + `javalibs-internal`.
- `application` gọi domain + port; đánh dấu transaction; **không** biết HTTP.
- `api` chỉ dịch HTTP ⇄ command/query; **không** chứa nghiệp vụ.
- `infrastructure` implement port; nơi duy nhất biết JPA/MinIO/mail.
- **Không package nào import `api`.**

### 5.3 javalibs artifact nào dùng ở lớp nào

| Lớp | Dùng | Ví dụ |
|---|---|---|
| `domain` | `javalibs-ddd-core`, `javalibs-internal` | `AggregateRoot`, `ValueObject`, `BusinessRule`, `Rules.check`, `Checks.notNull` |
| `application` | `javalibs-cqrs-core`, `javalibs-ddd-spring` | `Command`/`CommandHandler`, `@ApplicationService`, `DomainEventPublisher` |
| `api` | `javalibs-web`, `javalibs-search`, `javalibs-security` | `ApiResponse`, `PageResponse`, `BusinessErrorCode`, `ResourceNotFoundException`, `SearchQuery`, `@RequireRole`, `@CurrentUser` |
| `infrastructure` | Spring Data, `javalibs-cache`, `javalibs-resilience` | `JpaRepository`, `@Cacheable`, `CircuitBreakerRegistry` |
| `config` | mọi thứ | override bean bằng `@Bean` (javalibs tự lùi) |

### 5.4 Ghi chú thực dụng JPA ↔ domain

Chọn **một** hướng cho **toàn bộ** dự án và ghi vào README:

- **Thực dụng (khuyến nghị):** gắn `@Entity`/`@Table` trực tiếp lên aggregate trong `domain/model`; repository port `extends JpaRepository<Certification, UUID>, JpaSpecificationExecutor<Certification>`. Đổi lại `domain` có dependency JPA — chấp nhận được, và **dùng được ngay `javalibs-search`**.
- **Thuần khiết:** `domain` 100% sạch, `infrastructure/persistence` có JPA entity riêng + mapper 2 chiều. Chỉ đáng khi nghiệp vụ cực phức tạp.

> certi-master hiện đã trộn (entity là JPA). Khuyến nghị chọn **thực dụng** để đỡ viết mapper, và nhất quán toàn dự án.

---

## 6. `pom.xml` mới — hạ Boot + khai báo javalibs

### 6.1 Các thay đổi khi hạ 4.0.6 → 3.5.3

| Hiện tại (Boot 4) | Đổi thành (Boot 3.5.3) |
|---|---|
| `spring-boot-starter-parent:4.0.6` | `spring-boot-starter-parent:3.5.3` |
| `spring-modulith:2.0.6` | `spring-modulith:1.3.x` (bản cho Boot 3.5) |
| `spring-boot-starter-webmvc` | `spring-boot-starter-web` |
| `spring-boot-starter-restclient` | (gộp trong `-web`; RestClient có sẵn) |
| `spring-boot-starter-flyway` | (bỏ — dùng `javalibs-persistence-spring-boot-starter`) |
| `spring-boot-starter-micrometer-metrics` | (gộp trong `-actuator` + registry prometheus) |
| `spring-boot-starter-opentelemetry` | (gộp trong `javalibs-observability`; thêm OTLP exporter khi cần) |
| `spring-boot-starter-security-oauth2-resource-server` | giữ **chỉ nếu** dùng mode `oauth2-resource-server`; mặc định javalibs mode `jwt` không cần |

### 6.2 pom.xml khung

```xml
<project>
  <parent>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-parent</artifactId>
    <version>3.5.3</version>
    <relativePath/>
  </parent>

  <groupId>com.certimaster</groupId>
  <artifactId>certi-master</artifactId>
  <version>2.0.0-SNAPSHOT</version>

  <properties>
    <java.version>21</java.version>
    <spring-modulith.version>1.3.4</spring-modulith.version>
  </properties>

  <dependencyManagement>
    <dependencies>
      <!-- BOM javalibs: từ đây mọi artifact io.javalibs KHÔNG cần version -->
      <dependency>
        <groupId>io.javalibs</groupId>
        <artifactId>javalibs-dependencies</artifactId>
        <version>1.0.0-SNAPSHOT</version>
        <type>pom</type>
        <scope>import</scope>
      </dependency>
      <dependency>
        <groupId>org.springframework.modulith</groupId>
        <artifactId>spring-modulith-bom</artifactId>
        <version>${spring-modulith.version}</version>
        <type>pom</type>
        <scope>import</scope>
      </dependency>
    </dependencies>
  </dependencyManagement>

  <dependencies>
    <!-- ── Bộ nền javalibs (gần như mọi app đều cần) ── -->
    <dependency><groupId>io.javalibs</groupId><artifactId>javalibs-web-spring-boot-starter</artifactId></dependency>
    <dependency><groupId>io.javalibs</groupId><artifactId>javalibs-security-spring-boot-starter</artifactId></dependency>
    <dependency><groupId>io.javalibs</groupId><artifactId>javalibs-openapi-spring-boot-starter</artifactId></dependency>
    <dependency><groupId>io.javalibs</groupId><artifactId>javalibs-observability-spring-boot-starter</artifactId></dependency>
    <dependency><groupId>io.javalibs</groupId><artifactId>javalibs-persistence-spring-boot-starter</artifactId></dependency>

    <!-- ── Theo nhu cầu certi-master ── -->
    <dependency><groupId>io.javalibs</groupId><artifactId>javalibs-search-spring-boot-starter</artifactId></dependency>
    <dependency><groupId>io.javalibs</groupId><artifactId>javalibs-cqrs-spring-boot-starter</artifactId></dependency>
    <dependency><groupId>io.javalibs</groupId><artifactId>javalibs-ddd-spring</artifactId></dependency>
    <dependency><groupId>io.javalibs</groupId><artifactId>javalibs-cache-spring-boot-starter</artifactId></dependency>
    <!-- javalibs-cache KHÔNG kéo redis -> thêm: -->
    <dependency><groupId>org.springframework.boot</groupId><artifactId>spring-boot-starter-data-redis</artifactId></dependency>

    <!-- ── Hạ tầng Boot còn thiếu ── -->
    <dependency><groupId>org.springframework.boot</groupId><artifactId>spring-boot-starter-data-jpa</artifactId></dependency>
    <dependency><groupId>org.springframework.boot</groupId><artifactId>spring-boot-starter-mail</artifactId></dependency>
    <dependency><groupId>org.springframework.modulith</groupId><artifactId>spring-modulith-starter-jpa</artifactId></dependency>
    <dependency><groupId>io.micrometer</groupId><artifactId>micrometer-registry-prometheus</artifactId></dependency>
    <dependency><groupId>org.postgresql</groupId><artifactId>postgresql</artifactId><scope>runtime</scope></dependency>

    <!-- ── Issue JWT (javalibs chỉ validate) ── -->
    <dependency><groupId>io.jsonwebtoken</groupId><artifactId>jjwt-api</artifactId><version>0.12.3</version></dependency>
    <dependency><groupId>io.jsonwebtoken</groupId><artifactId>jjwt-impl</artifactId><version>0.12.3</version><scope>runtime</scope></dependency>
    <dependency><groupId>io.jsonwebtoken</groupId><artifactId>jjwt-jackson</artifactId><version>0.12.3</version><scope>runtime</scope></dependency>

    <!-- ── Nghiệp vụ riêng ── -->
    <dependency><groupId>io.minio</groupId><artifactId>minio</artifactId><version>8.5.7</version></dependency>
    <dependency><groupId>com.vladsch.flexmark</groupId><artifactId>flexmark-all</artifactId><version>0.64.8</version></dependency>
    <dependency><groupId>com.googlecode.owasp-java-html-sanitizer</groupId><artifactId>owasp-java-html-sanitizer</artifactId><version>20240325.1</version></dependency>

    <!-- ── Test ── -->
    <dependency><groupId>io.javalibs</groupId><artifactId>javalibs-test</artifactId><scope>test</scope></dependency>
    <dependency><groupId>io.javalibs</groupId><artifactId>javalibs-security-test</artifactId><scope>test</scope></dependency>
    <dependency><groupId>org.springframework.modulith</groupId><artifactId>spring-modulith-starter-test</artifactId><scope>test</scope></dependency>
  </dependencies>
</project>
```

> Không cần `@Import`/`@ComponentScan` cho javalibs — Boot tự quét `AutoConfiguration.imports` của từng starter.

---

## 7. Tích hợp Security — điểm tinh tế nhất

**javalibs-security chỉ *validate* token, không *issue* token.** Nên trách nhiệm chia đôi:

| Việc | Ai làm | Ở đâu |
|---|---|---|
| **Đăng nhập, ký JWT (RSA), refresh, revoke** | certi-master (giữ) | `identity/infrastructure/security/JwtIssuer` |
| **Validate token mỗi request, dựng `UserContext`, `@CurrentUser`, `@RequireRole`, filter chain** | javalibs-security (auto) | `io.javalibs.security.*` |

**Hợp đồng để hai bên khớp nhau:**
1. Identity **ký** bằng RSA private key. javalibs **validate** bằng public key: đặt `javalibs.security.jwt.public-key` = RSA public PEM tương ứng.
2. Token phải chứa claim roles ở đúng tên javalibs đọc: mặc định `roles-claim: roles`. Khi ký, nhét `roles` là JSON array (`["ADMIN","USER"]`).
3. `sub` = userId, `preferred_username`/`email` theo `username-claim`/`email-claim` — hoặc chỉnh property cho khớp claim identity đang phát.
4. **Logout/revoke:** bật `javalibs.security.blacklist.mode: redis`; endpoint logout gọi `TokenBlacklist.revoke(jti, expiresAt)` (token phải có claim `jti`).

```java
// identity/api/AuthController.java  — issue ở identity
@PostMapping("/login")
public ApiResponse<TokenResponse> login(@Valid @RequestBody LoginRequest req) {
    var tokens = commandBus.dispatch(new LoginCommand(req.email(), req.password()));
    return ApiResponse.ok(tokens); // access token ký RSA, có claim roles + jti
}

// bất kỳ context nào — consume qua javalibs
@GetMapping("/me")
public ApiResponse<ProfileResponse> me(@CurrentUser UserContext user) {
    return ApiResponse.ok(queryBus.ask(new GetProfileQuery(user.userId())));
}

@RequireRole("ADMIN")
@DeleteMapping("/{id}")
public void delete(@PathVariable UUID id) { commandBus.dispatch(new DeleteExamCommand(id)); }
```

> **Bẫy:** nếu bạn override `javalibs.security.permit-all`, nó **thay thế toàn bộ** danh sách mặc định → nhớ thêm lại swagger paths (`/swagger-ui.html`, `/swagger-ui/**`, `/v3/api-docs/**`) và các endpoint public của identity (`/api/v1/auth/**`).

---

## 8. Catalog mã lỗi nghiệp vụ (`BusinessErrorCode`)

Thay chuỗi mã lỗi tuỳ tiện (`"DUPLICATE_RESOURCE"`, `"INVALID_INPUT"`...) bằng catalog `ERR-<DOMAIN>-<SEQ>` ổn định cho frontend, **tự xuất hiện trong OpenAPI**.

```java
// learning/api/LearningErrors.java
public final class LearningErrors {
    public static final BusinessErrorCode CERT_NOT_FOUND  = BusinessErrorCode.of("ERR-LEARNING-001", 404);
    public static final BusinessErrorCode EXAM_LOCKED     = BusinessErrorCode.of("ERR-LEARNING-002", 409);
    public static final BusinessErrorCode SESSION_EXPIRED = BusinessErrorCode.of("ERR-LEARNING-003", 410);
    private LearningErrors() {}
}
// ném: throw new ApiException(LearningErrors.EXAM_LOCKED, "Exam already submitted");
//     throw new ResourceNotFoundException("Certification", id); // -> ERR_RESOURCE_NOT_FOUND 404
```

Mỗi context có một file `*Errors.java` + một mục trong `ERROR-CODES.md` ở gốc dự án (đồng bộ 2 nơi).

---

## 9. `application.yml` mới

```yaml
spring:
  application:
    name: certi-master
  datasource:
    url: jdbc:postgresql://localhost:5433/certimaster
    username: ${DB_USER}
    password: ${DB_PASSWORD}
  jpa:
    hibernate:
      ddl-auto: validate          # schema do Flyway quản lý
  data:
    redis:
      host: ${REDIS_HOST:localhost}
      port: ${REDIS_PORT:6379}
      password: ${REDIS_PASSWORD:}

javalibs:
  security:
    mode: jwt
    jwt:
      public-key: ${JWT_PUBLIC_KEY}      # PEM RSA — khớp private key identity dùng để ký
      roles-claim: roles
    blacklist:
      mode: redis                        # bật revoke khi logout
    permit-all:                          # ghi đè -> nhớ thêm lại swagger + auth public
      - /actuator/health
      - /actuator/health/**
      - /actuator/info
      - /error
      - /v3/api-docs/**
      - /swagger-ui/**
      - /swagger-ui.html
      - /api/v1/auth/**
  web:
    cors:
      enabled: true
      allowed-origins: ${CORS_ORIGINS}
      allow-credentials: true
  search:
    default-page-size: 20
    max-page-size: 100
  cache:
    provider: redis
    caches:
      certifications: { ttl: 1h }
      trending-posts: { ttl: 5m }
  observability:
    metrics:
      environment: ${APP_ENV:dev}
      common-tags:
        app: certi-master
  openapi:
    title: CertiMaster API
    version: v1
    contact: { name: CertiMaster, email: binhzero0603@gmail.com }

management:
  endpoints:
    web:
      exposure:
        include: health,info,prometheus,modulith

logging:
  pattern:
    level: "%5p [%X{correlationId}] [%X{traceId}/%X{spanId}]"   # observability cần %X{correlationId}
```

---

## 10. Triển khai từng bước

> Làm **theo module dọc (vertical slice)**, không làm theo tầng ngang. Hoàn thiện `learning` end-to-end trước rồi nhân bản pattern sang các context còn lại.

### Bước 0 — Chuẩn bị
- [ ] Build javalibs về local repo: `cd /Users/tanvx/Dev/javalibs-root && mvn clean install`.
- [ ] Tạo nhánh mới `rebuild/javalibs`. **Không** làm trên `master`.
- [ ] Sinh cặp RSA keypair cho JWT; lưu private key cho identity, public key cho `javalibs.security.jwt.public-key`.

### Bước 1 — Hạ Boot + pom
- [ ] Thay `pom.xml` theo [§6](#6-pomxml-mới--hạ-boot--khai-báo-javalibs). Bump version app `2.0.0-SNAPSHOT`.
- [ ] `mvn dependency:tree` — xác nhận Boot 3.5.3, không còn starter Boot-4-only.
- [ ] `mvn clean compile` (sẽ đỏ nhiều chỗ import `shared.*` — bình thường, sẽ sửa dần).

### Bước 2 — Cấu hình
- [ ] Viết `application.yml` theo [§9](#9-applicationyml-mới); tách `application-local.yml` (DB localhost, log DEBUG).
- [ ] `src/test/resources/application-test.yml`: đặt `javalibs.security.jwt.secret: javalibs-test-secret-key-0123456789abcdef` (= `JwtTestFactory.DEFAULT_TEST_SECRET`) để test dùng HMAC cho gọn.

### Bước 3 — Dọn `shared/`
- [ ] Xoá các file theo bảng [§4.1](#41-xoá--thay-bằng-javalibs) (GlobalExceptionHandler, dto envelope, exception cây, security validate, OpenApiConfig...).
- [ ] Đổi mọi import `com.certimaster.shared.dto.*` → `io.javalibs.web.*`, `com.certimaster.shared.exception.*` → `io.javalibs.web.exception.*`.
- [ ] Giữ lại kernel tối thiểu ([§4.3](#43-shared-teo-lại-còn-gì)).

### Bước 4 — Refactor 1 context mẫu end-to-end (`learning`)
- [ ] Sắp lại package thành `api / application / domain / infrastructure` theo [§5.1](#51-cây-thư-mục-một-context-mẫu-learning).
- [ ] `domain/model`: cho aggregate `extends AggregateRoot<UUID>`; bất biến bằng `Rules.check(new ExamMustHaveQuestions(...))`; phát event bằng `registerEvent(...)`.
- [ ] `domain/*Repository.java`: port `extends JpaRepository<..>, JpaSpecificationExecutor<..>` (hướng thực dụng).
- [ ] `application/command|query`: mỗi use case = 1 record `Command`/`Query` + 1 class `@Component` handler (⚠ **không dùng lambda** — type erasure). Handler là nơi duy nhất `@Transactional`.
- [ ] `api/*Controller`: mỏng, chỉ `commandBus.dispatch(...)` / `queryBus.ask(...)`; trả `ApiResponse.ok(...)` / `PageResponse.of(...)`.
- [ ] `api/LearningErrors.java`: catalog `BusinessErrorCode` ([§8](#8-catalog-mã-lỗi-nghiệp-vụ-businesserrorcode)).
- [ ] Named interface `lookup` giữ nguyên (productivity đang phụ thuộc) — trả DTO.

### Bước 5 — Nhân bản sang các context còn lại
- [ ] `content` (từ engagement, bỏ notification), `productivity`, `identity`.
- [ ] **`identity`:** dựng `infrastructure/security/JwtIssuer` (ký RSA, claim `roles`+`jti`), `AuthController` login/refresh/logout; logout gọi `TokenBlacklist.revoke`. Xem [§7](#7-tích-hợp-security--điểm-tinh-tế-nhất).
- [ ] **Tách `notification`:** tạo context mới; chuyển `Notification` + 3 listener; đổi listener sang `@ApplicationModuleListener` nghe event từ learning/productivity/content.

### Bước 6 — Event xuyên context
- [ ] Trong context: domain phát event → `application` publish qua `DomainEventPublisher.publishFrom(aggregate)` (javalibs-ddd, wrap `ApplicationEventPublisher`).
- [ ] Xuyên context: bên nhận dùng `@ApplicationModuleListener` (Spring Modulith, chạy sau commit, có `event_publication` để retry).
- [ ] **In-module dispatch** (controller → use case) dùng `CommandBus`/`QueryBus` (javalibs-cqrs). **Cross-module** dùng event, không gọi bus của module khác.

### Bước 7 — Search
- [ ] Repository cần lọc động: `extends JpaSpecificationExecutor`. Controller nhận `SearchQuery` (argument resolver tự có), gọi `SpecificationBuilder.toSpecification(q)` + `PageRequests.of(q)`.
- [ ] Thêm `@ExceptionHandler(SearchParseException.class)` → 400 (nếu muốn thông báo rõ). Lưu ý: **không** lọc được collection `@OneToMany`.

### Bước 8 — Migration
- [ ] Giữ nguyên `db/migration/V1..V12`. Nếu muốn sạch (dựng mới), có thể gộp thành `V202607xxxxxx__baseline.sql`. `javalibs-persistence` sẽ ép `clean-disabled` + validate naming.
- [ ] `ddl-auto: validate`; giữ `SchemaMigrationIntegrationTest` để đảm bảo entity khớp schema.

### Bước 9 — OpenAPI
- [ ] Xoá `OpenApiConfig`. Cấu hình qua `javalibs.openapi.*`. Bearer scheme + global error responses tự có. Kiểm tra `/swagger-ui.html`.

### Bước 10 — Test (kim tự tháp 3 tầng)
- [ ] `domain/` — JUnit thuần, 0 Spring (nhiều nhất, nhanh nhất).
- [ ] `application/` — Mockito mock port; verify nghiệp vụ + event enqueue.
- [ ] `api/*IT.java` — `extends BaseIntegrationTest` (Postgres Testcontainers); token qua `JwtTestFactory.create().roles("ADMIN").bearer()`.
- [ ] Giữ `@ApplicationModuleTest` cho từng module + `ModularityTests` (verify không có cycle sau khi tách notification).
- [ ] **Đừng** test lại thứ javalibs đã test (format JSON lỗi, parse token, relay...).

### Bước 11 — Hoàn tất
- [ ] `mvn verify` xanh.
- [ ] Cập nhật `README.md` (Boot 3.5.3, 5 context, javalibs). Tạo `ERROR-CODES.md`.
- [ ] Cập nhật `.github/workflows` cho JDK 21 + `mvn verify`.

---

## 11. Dùng javalibs hiệu quả — Do / Don't

| ✅ Nên | ❌ Tránh |
|---|---|
| Config qua `javalibs.*` trong yaml trước tiên | Tự viết `@Bean` khi một property đã đủ |
| Ghi đè bằng `@Bean` cùng type, gom vào `config/` | Rải override khắp nơi |
| Ném `ApiException`/`ResourceNotFoundException` của javalibs | Tự viết `GlobalExceptionHandler` |
| `BusinessErrorCode.of("ERR-XXX-nnn", status)` | Chuỗi mã lỗi tuỳ tiện |
| `@CurrentUser UserContext` / `UserContextHolder.require()` | Đọc `SecurityContextHolder` thủ công |
| `Checks.notNull`, `Strings.isBlank`, `Ids.uuid` (javalibs-internal) | Tạo `shared/util` mới |
| Handler CQRS là **class** `@Component` | Lambda handler (type erasure → bus không tìm được) |
| Cross-module bằng Modulith event | Gọi service module khác trực tiếp |
| `@EnableCaching` trên main class khi dùng cache | Quên `@EnableCaching` (cache im lặng không chạy) |
| Cache DTO | Cache JPA entity (serializer nhúng `@class`, refactor là vỡ) |

**Mục tiêu cuối:** một context lý tưởng có `config/` **rỗng** — toàn bộ khung do javalibs lo, code còn lại 100% là nghiệp vụ CertiMaster.

---

## 12. Rủi ro & lưu ý

| Rủi ro | Giảm thiểu |
|---|---|
| **Hạ Boot 4→3.5.3** kéo theo đổi tên starter, downgrade modulith 2.0.6→1.3.x, có thể lệch API vài chỗ | Làm ở [Bước 1] trước, cô lập; `mvn dependency:tree` kiểm tra; chạy app rỗng trước khi refactor domain |
| javalibs còn `1.0.0-SNAPSHOT` (chưa release) | Build local `mvn install`; hoặc publish lên Nexus nội bộ (xem README javalibs) |
| Token identity **ký** phải khớp cái javalibs **validate** (key + claim `roles`/`jti`) | Test tích hợp login→gọi API bảo vệ ngay từ đầu ([§7](#7-tích-hợp-security--điểm-tinh-tế-nhất)) |
| Override `permit-all` thay thế toàn bộ danh sách | Luôn thêm lại swagger + `/api/v1/auth/**` |
| `javalibs-cache` không kéo redis; `@EnableCaching` bắt buộc | Thêm `spring-boot-starter-data-redis` + `@EnableCaching` |
| Tách `notification` có thể lộ cycle phụ thuộc | `ModularityTests` sẽ báo; giữ giao tiếp một chiều bằng event |

---

## Phụ lục — Ma trận tính năng ↔ starter javalibs

| Cần gì | Starter |
|---|---|
| Chuẩn lỗi JSON, ApiResponse, PageResponse, logging, CORS | `javalibs-web-spring-boot-starter` |
| JWT validate, `@CurrentUser`, `@RequireRole`, revoke | `javalibs-security-spring-boot-starter` (+ `javalibs-security-test`) |
| DDD base (AggregateRoot, ValueObject, BusinessRule, event publisher) | `javalibs-ddd-spring` |
| CommandBus / QueryBus | `javalibs-cqrs-spring-boot-starter` |
| Filter động HTTP → JPA Specification | `javalibs-search-spring-boot-starter` |
| Swagger + bearer + global error responses | `javalibs-openapi-spring-boot-starter` |
| Correlation id, MDC, common metric tags | `javalibs-observability-spring-boot-starter` |
| Flyway an toàn (clean-disabled, naming) | `javalibs-persistence-spring-boot-starter` |
| Redis/Caffeine cache chuẩn hoá | `javalibs-cache-spring-boot-starter` (+ redis) |
| Circuit breaker / retry / rate limiter | `javalibs-resilience-spring-boot-starter` |
| Tiện ích thuần Java (Strings/Checks/Dates/Ids) | `javalibs-internal` (transitive) |
| BaseIntegrationTest + Testcontainers Postgres | `javalibs-test` (scope test) |

> Tham chiếu chi tiết từng module: `/Users/tanvx/Dev/javalibs-root/docs/modules/*.md`. Tham chiếu cấu hình đầy đủ: `docs/configuration-reference.md`.

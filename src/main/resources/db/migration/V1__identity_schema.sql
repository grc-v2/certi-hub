-- Identity schema: users, roles and auth tokens, plus shared infrastructure
-- (audit log, Spring Modulith event publication registry).
--
-- Reconstructed from the JPA entities in com.certimaster.identity.domain.entity
-- so the schema is fully reproducible from the repository. Existing databases
-- that predate Flyway are baselined at version 1 (baseline-on-migrate), so this
-- script only ever runs against a fresh database; every statement is still
-- idempotent (IF NOT EXISTS) to match the conventions of later migrations.

-- Users and roles ------------------------------------------------------------

CREATE TABLE IF NOT EXISTS users (
    id                          BIGSERIAL PRIMARY KEY,
    email                       VARCHAR(300) NOT NULL,
    username                    VARCHAR(100) NOT NULL,
    password_hash               VARCHAR(255) NOT NULL,
    full_name                   VARCHAR(300),
    avatar_url                  VARCHAR(500),
    phone                       VARCHAR(20),
    status                      VARCHAR(20),
    email_verified              BOOLEAN,
    last_login_date             TIMESTAMP,
    last_password_change_date   TIMESTAMP,
    deleted_at                  TIMESTAMP,
    bio                         TEXT,
    timezone                    VARCHAR(50),
    preferred_language          VARCHAR(10),
    professional_info           JSONB,
    learning_preferences        JSONB,

    created_at                  TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at                  TIMESTAMP,
    created_by                  VARCHAR(50),
    updated_by                  VARCHAR(50),

    CONSTRAINT uk_users_email UNIQUE (email),
    CONSTRAINT uk_users_username UNIQUE (username)
);

CREATE TABLE IF NOT EXISTS roles (
    id              BIGSERIAL PRIMARY KEY,
    code            VARCHAR(50) NOT NULL,
    name            VARCHAR(100) NOT NULL,
    description     TEXT,

    created_at      TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at      TIMESTAMP,
    created_by      VARCHAR(50),
    updated_by      VARCHAR(50),

    CONSTRAINT uk_roles_code UNIQUE (code)
);

CREATE TABLE IF NOT EXISTS role_permissions (
    role_id         BIGINT NOT NULL REFERENCES roles(id),
    permission      VARCHAR(255)
);

CREATE TABLE IF NOT EXISTS user_roles (
    user_id         BIGINT NOT NULL REFERENCES users(id),
    role_id         BIGINT NOT NULL REFERENCES roles(id),

    CONSTRAINT pk_user_roles PRIMARY KEY (user_id, role_id)
);

-- Auth tokens ----------------------------------------------------------------

CREATE TABLE IF NOT EXISTS refresh_tokens (
    id              BIGSERIAL PRIMARY KEY,
    user_id         BIGINT NOT NULL,
    token           VARCHAR(500) NOT NULL,
    expires_at      TIMESTAMP NOT NULL,
    created_at      TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT uk_refresh_tokens_token UNIQUE (token)
);

CREATE TABLE IF NOT EXISTS email_verification_tokens (
    id              BIGSERIAL PRIMARY KEY,
    user_id         BIGINT NOT NULL REFERENCES users(id),
    token           VARCHAR(500) NOT NULL,
    expires_at      TIMESTAMP NOT NULL,
    verified_at     TIMESTAMP,

    created_at      TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at      TIMESTAMP,
    created_by      VARCHAR(50),
    updated_by      VARCHAR(50),

    CONSTRAINT uk_email_verification_tokens_token UNIQUE (token)
);

CREATE INDEX IF NOT EXISTS idx_role_permissions_role ON role_permissions (role_id);
CREATE INDEX IF NOT EXISTS idx_refresh_tokens_user ON refresh_tokens (user_id);
CREATE INDEX IF NOT EXISTS idx_email_verification_tokens_user ON email_verification_tokens (user_id);

-- Spring Modulith event publication registry ---------------------------------
-- Backs the JPA event publication repository (spring-modulith-starter-jpa);
-- replaces the legacy outbox/inbox tables of the pre-modulith services.

CREATE TABLE IF NOT EXISTS event_publication (
    id                      UUID PRIMARY KEY,
    listener_id             TEXT NOT NULL,
    event_type              TEXT NOT NULL,
    serialized_event        TEXT NOT NULL,
    publication_date        TIMESTAMP WITH TIME ZONE NOT NULL,
    completion_date         TIMESTAMP WITH TIME ZONE,
    status                  VARCHAR(20),
    completion_attempts     INTEGER NOT NULL DEFAULT 0,
    last_resubmission_date  TIMESTAMP WITH TIME ZONE
);

CREATE INDEX IF NOT EXISTS idx_event_publication_serialized_event ON event_publication USING hash (serialized_event);
CREATE INDEX IF NOT EXISTS idx_event_publication_completion_date ON event_publication (completion_date);

-- Audit log infrastructure for admin actions ---------------------------------
-- Written by AuditInterceptor on every /api/v1/admin/** request.

CREATE TABLE IF NOT EXISTS audit_log (
    id              BIGSERIAL PRIMARY KEY,
    actor_id        BIGINT,
    actor_username  VARCHAR(100),
    action          VARCHAR(100) NOT NULL,
    resource_type   VARCHAR(50),
    resource_id     VARCHAR(100),
    http_method     VARCHAR(10),
    http_path       VARCHAR(500),
    query_string    VARCHAR(1000),
    ip_address      VARCHAR(64),
    user_agent      VARCHAR(500),
    status_code     INTEGER,
    duration_ms     BIGINT,
    service_name    VARCHAR(50),
    occurred_at     TIMESTAMP NOT NULL,

    created_at      TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at      TIMESTAMP,
    created_by      VARCHAR(50),
    updated_by      VARCHAR(50)
);

CREATE INDEX IF NOT EXISTS idx_audit_log_actor        ON audit_log (actor_id);
CREATE INDEX IF NOT EXISTS idx_audit_log_resource     ON audit_log (resource_type, resource_id);
CREATE INDEX IF NOT EXISTS idx_audit_log_created_at   ON audit_log (created_at);
CREATE INDEX IF NOT EXISTS idx_audit_log_occurred_at  ON audit_log (occurred_at);

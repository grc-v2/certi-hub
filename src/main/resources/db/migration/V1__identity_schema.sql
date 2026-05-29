-- Phase 0: Audit log infrastructure for admin actions.
-- Written by AuditInterceptor on every /api/v1/admin/** request.
--
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

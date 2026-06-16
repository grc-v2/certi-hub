-- Comments and in-app notifications for the engagement module.
-- Following the productivity/engagement convention, post_id and user_id are plain
-- BIGINT references (no cross-schema FK to legacy posts / identity users).

CREATE TABLE IF NOT EXISTS post_comments (
    id                  BIGSERIAL PRIMARY KEY,
    post_id             BIGINT NOT NULL,
    parent_id           BIGINT REFERENCES post_comments(id) ON DELETE CASCADE,
    author_id           BIGINT NOT NULL,
    author_name         VARCHAR(100),
    content             TEXT NOT NULL,

    created_at          TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at          TIMESTAMP,
    created_by          VARCHAR(50),
    updated_by          VARCHAR(50)
);

CREATE INDEX IF NOT EXISTS idx_post_comments_post_id ON post_comments(post_id);
CREATE INDEX IF NOT EXISTS idx_post_comments_parent_id ON post_comments(parent_id);

CREATE TABLE IF NOT EXISTS notifications (
    id                  BIGSERIAL PRIMARY KEY,
    user_id             BIGINT NOT NULL,
    type                VARCHAR(40) NOT NULL,
    title               VARCHAR(255) NOT NULL,
    message             TEXT,
    is_read             BOOLEAN NOT NULL DEFAULT FALSE,
    related_entity_id   VARCHAR(100),

    created_at          TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at          TIMESTAMP,
    created_by          VARCHAR(50),
    updated_by          VARCHAR(50)
);

CREATE INDEX IF NOT EXISTS idx_notifications_user_id ON notifications(user_id);
CREATE INDEX IF NOT EXISTS idx_notifications_user_unread ON notifications(user_id, is_read);

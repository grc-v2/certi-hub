-- Engagement schema: blog posts, post tags and bookmarks. Reconstructed from
-- the JPA entities in com.certimaster.engagement.domain.entity so the schema is
-- fully reproducible from the repository (the original schema was migrated from
-- the legacy database). Legacy outbox/processed event tables were intentionally
-- not copied; Spring Modulith manages event publication through its Event
-- Publication Registry (see V1). Comments and in-app notifications are created
-- later in V8. Existing databases are baselined at version 1, so this script
-- only runs against a fresh database; statements are idempotent regardless.
--
-- Convention: user_id/author_id columns are plain BIGINT (no cross-module FK to
-- the identity schema).

CREATE TABLE IF NOT EXISTS posts (
    id                      BIGSERIAL PRIMARY KEY,
    title                   VARCHAR(255) NOT NULL,
    slug                    VARCHAR(255) NOT NULL,
    content                 TEXT NOT NULL,
    content_html            TEXT,
    excerpt                 TEXT,
    featured_image          VARCHAR(500),
    author_id               BIGINT NOT NULL,
    status                  VARCHAR(50),
    published_at            TIMESTAMP,
    views_count             INTEGER,
    likes_count             INTEGER,
    comments_count          INTEGER,
    reading_time_minutes    INTEGER,
    seo_title               VARCHAR(255),
    seo_description         TEXT,
    seo_keywords            TEXT,
    content_type            VARCHAR(30),
    difficulty_level        VARCHAR(20),
    subtitle                VARCHAR(500),
    cover_image_url         VARCHAR(500),
    cover_image_alt         VARCHAR(255),
    table_of_contents       JSONB,
    word_count              INTEGER,
    unique_view_count       INTEGER,
    bookmark_count          INTEGER,
    share_count             INTEGER,
    co_author_ids           JSONB,
    reviewer_id             BIGINT,
    scheduled_publish_at    TIMESTAMP,
    noindex                 BOOLEAN,
    pinned                  BOOLEAN,
    featured                BOOLEAN,
    category                VARCHAR(50),
    series_name             VARCHAR(200),
    series_order            INTEGER,
    cert_refs               JSONB,

    created_at              TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at              TIMESTAMP,
    created_by              VARCHAR(50),
    updated_by              VARCHAR(50),

    CONSTRAINT uk_posts_slug UNIQUE (slug),
    CONSTRAINT chk_posts_status CHECK (status IN ('DRAFT', 'IN_REVIEW', 'SCHEDULED', 'PUBLISHED', 'ARCHIVED')),
    CONSTRAINT chk_posts_content_type CHECK (content_type IN ('NEWS', 'GUIDE', 'TIPS', 'CONCEPT_DEEP_DIVE', 'EXAM_GUIDE',
                                                              'COMPARISON', 'HANDS_ON_TUTORIAL', 'NEWS_UPDATE', 'CHEAT_SHEET')),
    CONSTRAINT chk_posts_difficulty_level CHECK (difficulty_level IN ('BEGINNER', 'INTERMEDIATE', 'ADVANCED'))
);

CREATE TABLE IF NOT EXISTS post_tags (
    id                          BIGSERIAL PRIMARY KEY,
    name                        VARCHAR(100) NOT NULL,
    slug                        VARCHAR(100) NOT NULL,
    tag_category                VARCHAR(30),
    description                 TEXT,
    parent_tag_id               BIGINT REFERENCES post_tags(id),
    mapped_provider_id          BIGINT,
    mapped_certification_id     BIGINT,
    mapped_domain_id            BIGINT,
    post_count                  INTEGER,
    display_order               INTEGER,

    created_at                  TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at                  TIMESTAMP,
    created_by                  VARCHAR(50),
    updated_by                  VARCHAR(50),

    CONSTRAINT uk_post_tags_name UNIQUE (name),
    CONSTRAINT uk_post_tags_slug UNIQUE (slug),
    CONSTRAINT chk_post_tags_category CHECK (tag_category IN ('PROVIDER', 'CERTIFICATION', 'DOMAIN', 'TOPIC', 'GENERAL'))
);

-- Join table with a composite primary key (PostTagMapping has no audit fields).
CREATE TABLE IF NOT EXISTS post_tag_mappings (
    post_id             BIGINT NOT NULL REFERENCES posts(id),
    tag_id              BIGINT NOT NULL REFERENCES post_tags(id),

    CONSTRAINT pk_post_tag_mappings PRIMARY KEY (post_id, tag_id)
);

CREATE TABLE IF NOT EXISTS bookmarks (
    id                  BIGSERIAL PRIMARY KEY,
    user_id             BIGINT NOT NULL,
    post_id             BIGINT NOT NULL REFERENCES posts(id),
    notes               TEXT,

    created_at          TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at          TIMESTAMP,
    created_by          VARCHAR(50),
    updated_by          VARCHAR(50),

    CONSTRAINT uk_bookmarks_user_post UNIQUE (user_id, post_id)
);

CREATE INDEX IF NOT EXISTS idx_posts_author ON posts (author_id);
CREATE INDEX IF NOT EXISTS idx_posts_status ON posts (status);
CREATE INDEX IF NOT EXISTS idx_posts_published_at ON posts (published_at);
CREATE INDEX IF NOT EXISTS idx_post_tags_parent ON post_tags (parent_tag_id);
CREATE INDEX IF NOT EXISTS idx_post_tag_mappings_tag ON post_tag_mappings (tag_id);
CREATE INDEX IF NOT EXISTS idx_bookmarks_user ON bookmarks (user_id);
CREATE INDEX IF NOT EXISTS idx_bookmarks_post ON bookmarks (post_id);

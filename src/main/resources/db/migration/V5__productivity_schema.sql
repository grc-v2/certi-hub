-- Productivity schema — personal tracker dashboard (Todo, Resource Hub, Pomodoro).
-- Backs the new bounded context com.certimaster.productivity. Following the
-- engagement convention, user_id is a plain BIGINT (no cross-module FK to the
-- identity schema). Resource Hub merges the old "notes" + "bookmark" ideas into a
-- single tracker_resources table classified by kind (YOUTUBE/GITHUB/BLOG/NOTE).

CREATE TABLE IF NOT EXISTS todo_tasks (
    id                  BIGSERIAL PRIMARY KEY,
    user_id             BIGINT NOT NULL,
    title               VARCHAR(255) NOT NULL,
    description         TEXT,
    due_date            DATE,
    status              VARCHAR(20) NOT NULL DEFAULT 'BACKLOG',
    order_index         INTEGER NOT NULL DEFAULT 0,
    done_at             TIMESTAMP,

    created_at          TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at          TIMESTAMP,
    created_by          VARCHAR(50),
    updated_by          VARCHAR(50),

    CONSTRAINT chk_todo_tasks_status CHECK (status IN ('BACKLOG', 'TODO', 'DOING', 'DONE'))
);

CREATE TABLE IF NOT EXISTS tracker_collections (
    id                  BIGSERIAL PRIMARY KEY,
    user_id             BIGINT NOT NULL,
    name                VARCHAR(100) NOT NULL,

    created_at          TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at          TIMESTAMP,
    created_by          VARCHAR(50),
    updated_by          VARCHAR(50),

    CONSTRAINT uk_tracker_collections_user_name UNIQUE (user_id, name)
);

CREATE TABLE IF NOT EXISTS tracker_resources (
    id                  BIGSERIAL PRIMARY KEY,
    user_id             BIGINT NOT NULL,
    kind                VARCHAR(16) NOT NULL,
    url                 VARCHAR(2048),
    title               VARCHAR(512),
    content             TEXT,
    collection_id       BIGINT REFERENCES tracker_collections(id) ON DELETE SET NULL,

    created_at          TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at          TIMESTAMP,
    created_by          VARCHAR(50),
    updated_by          VARCHAR(50),

    CONSTRAINT chk_tracker_resources_kind CHECK (kind IN ('YOUTUBE', 'GITHUB', 'BLOG', 'NOTE'))
);

CREATE TABLE IF NOT EXISTS pomodoro_settings (
    id                  BIGSERIAL PRIMARY KEY,
    user_id             BIGINT NOT NULL,
    preset              VARCHAR(16) NOT NULL DEFAULT 'CLASSIC',
    focus_minutes       INTEGER NOT NULL DEFAULT 25,
    break_minutes       INTEGER NOT NULL DEFAULT 5,

    created_at          TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at          TIMESTAMP,
    created_by          VARCHAR(50),
    updated_by          VARCHAR(50),

    CONSTRAINT uk_pomodoro_settings_user UNIQUE (user_id),
    CONSTRAINT chk_pomodoro_settings_preset CHECK (preset IN ('CLASSIC', 'DEEP'))
);

CREATE INDEX IF NOT EXISTS idx_todo_tasks_user ON todo_tasks (user_id);
CREATE INDEX IF NOT EXISTS idx_todo_tasks_user_status ON todo_tasks (user_id, status, order_index);
CREATE INDEX IF NOT EXISTS idx_tracker_collections_user ON tracker_collections (user_id);
CREATE INDEX IF NOT EXISTS idx_tracker_resources_user ON tracker_resources (user_id);
CREATE INDEX IF NOT EXISTS idx_tracker_resources_collection ON tracker_resources (collection_id);

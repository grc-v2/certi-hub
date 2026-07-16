-- Pomodoro session history for the productivity module. The timer runs client-side;
-- each finished (or abandoned) run is reported once and stored here to power the
-- stats widget (today + last-7-days focus totals). kind mirrors the client timer
-- phases (FOCUS/SHORT_BREAK/LONG_BREAK); completed=false marks an abandoned run.
-- user_id is a plain BIGINT (no cross-module FK), following the productivity
-- convention; task_id optionally links the session to the todo task worked on.

CREATE TABLE IF NOT EXISTS pomodoro_sessions (
    id                  BIGSERIAL PRIMARY KEY,
    user_id             BIGINT NOT NULL,
    task_id             BIGINT REFERENCES todo_tasks(id) ON DELETE SET NULL,
    kind                VARCHAR(16) NOT NULL DEFAULT 'FOCUS',
    started_at          TIMESTAMP NOT NULL,
    ended_at            TIMESTAMP NOT NULL,
    duration_seconds    INTEGER NOT NULL,
    completed           BOOLEAN NOT NULL DEFAULT TRUE,

    created_at          TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at          TIMESTAMP,
    created_by          VARCHAR(50),
    updated_by          VARCHAR(50),

    CONSTRAINT chk_pomodoro_sessions_kind CHECK (kind IN ('FOCUS', 'SHORT_BREAK', 'LONG_BREAK')),
    CONSTRAINT chk_pomodoro_sessions_duration CHECK (duration_seconds > 0)
);

CREATE INDEX IF NOT EXISTS idx_pomodoro_sessions_user_started ON pomodoro_sessions (user_id, started_at);
CREATE INDEX IF NOT EXISTS idx_pomodoro_sessions_task ON pomodoro_sessions (task_id);

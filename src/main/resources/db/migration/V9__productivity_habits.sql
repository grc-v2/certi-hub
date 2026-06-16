-- Daily habit tracker for the productivity module: a list of good habits with a
-- frequency (DAILY or WEEKLY on chosen weekdays), optional reminder time, and a
-- per-day check-in confirming the habit was performed. user_id is a plain BIGINT
-- (no cross-module FK), following the existing productivity convention.

CREATE TABLE IF NOT EXISTS habits (
    id                  BIGSERIAL PRIMARY KEY,
    user_id             BIGINT NOT NULL,
    name                VARCHAR(150) NOT NULL,
    color               VARCHAR(20),
    frequency           VARCHAR(16) NOT NULL DEFAULT 'DAILY',
    weekdays            VARCHAR(120),
    reminder_time       TIME,
    active              BOOLEAN NOT NULL DEFAULT TRUE,
    order_index         INTEGER NOT NULL DEFAULT 0,
    last_reminded_on    DATE,

    created_at          TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at          TIMESTAMP,
    created_by          VARCHAR(50),
    updated_by          VARCHAR(50),

    CONSTRAINT chk_habits_frequency CHECK (frequency IN ('DAILY', 'WEEKLY'))
);

CREATE INDEX IF NOT EXISTS idx_habits_user_id ON habits(user_id);
CREATE INDEX IF NOT EXISTS idx_habits_reminder ON habits(active, reminder_time);

CREATE TABLE IF NOT EXISTS habit_checkins (
    id                  BIGSERIAL PRIMARY KEY,
    habit_id            BIGINT NOT NULL REFERENCES habits(id) ON DELETE CASCADE,
    user_id             BIGINT NOT NULL,
    checkin_date        DATE NOT NULL,

    created_at          TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at          TIMESTAMP,
    created_by          VARCHAR(50),
    updated_by          VARCHAR(50),

    CONSTRAINT uk_habit_checkins_habit_date UNIQUE (habit_id, checkin_date)
);

CREATE INDEX IF NOT EXISTS idx_habit_checkins_user_date ON habit_checkins(user_id, checkin_date);

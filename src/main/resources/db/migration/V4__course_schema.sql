CREATE TABLE IF NOT EXISTS courses (
    id                  BIGSERIAL PRIMARY KEY,
    certification_id    BIGINT NOT NULL REFERENCES certifications(id),
    name                VARCHAR(255) NOT NULL,
    description         TEXT,
    instructor          VARCHAR(255),
    thumbnail_url       VARCHAR(500),
    duration_minutes    INTEGER,
    enrolled_count      INTEGER NOT NULL DEFAULT 0,
    is_published        BOOLEAN NOT NULL DEFAULT FALSE,

    created_at          TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at          TIMESTAMP,
    created_by          VARCHAR(50),
    updated_by          VARCHAR(50)
);

CREATE TABLE IF NOT EXISTS course_modules (
    id                  BIGSERIAL PRIMARY KEY,
    course_id           BIGINT NOT NULL REFERENCES courses(id) ON DELETE CASCADE,
    title               VARCHAR(255) NOT NULL,
    description         TEXT,
    order_index         INTEGER NOT NULL,

    created_at          TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at          TIMESTAMP,
    created_by          VARCHAR(50),
    updated_by          VARCHAR(50)
);

CREATE TABLE IF NOT EXISTS course_lessons (
    id                  BIGSERIAL PRIMARY KEY,
    module_id           BIGINT NOT NULL REFERENCES course_modules(id) ON DELETE CASCADE,
    title               VARCHAR(255) NOT NULL,
    description         TEXT,
    content             TEXT,
    video_url           VARCHAR(500),
    duration_minutes    INTEGER,
    order_index         INTEGER NOT NULL,
    lesson_type         VARCHAR(20) NOT NULL,

    created_at          TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at          TIMESTAMP,
    created_by          VARCHAR(50),
    updated_by          VARCHAR(50),

    CONSTRAINT chk_course_lessons_type CHECK (lesson_type IN ('VIDEO', 'ARTICLE', 'QUIZ'))
);

CREATE TABLE IF NOT EXISTS course_enrollments (
    id                      BIGSERIAL PRIMARY KEY,
    user_id                 BIGINT NOT NULL REFERENCES users(id),
    course_id               BIGINT NOT NULL REFERENCES courses(id) ON DELETE CASCADE,
    status                  VARCHAR(20) NOT NULL DEFAULT 'IN_PROGRESS',
    progress_percentage     NUMERIC(5, 2) NOT NULL DEFAULT 0,
    last_accessed_at        TIMESTAMP,
    enrolled_at             TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    created_at              TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at              TIMESTAMP,
    created_by              VARCHAR(50),
    updated_by              VARCHAR(50),

    CONSTRAINT uk_course_enrollments_user_course UNIQUE (user_id, course_id),
    CONSTRAINT chk_course_enrollments_status CHECK (status IN ('IN_PROGRESS', 'COMPLETED')),
    CONSTRAINT chk_course_enrollments_progress CHECK (progress_percentage >= 0 AND progress_percentage <= 100)
);

CREATE TABLE IF NOT EXISTS course_lesson_progress (
    id                  BIGSERIAL PRIMARY KEY,
    enrollment_id       BIGINT NOT NULL REFERENCES course_enrollments(id) ON DELETE CASCADE,
    lesson_id           BIGINT NOT NULL REFERENCES course_lessons(id) ON DELETE CASCADE,
    is_completed        BOOLEAN NOT NULL DEFAULT FALSE,
    completed_at        TIMESTAMP,

    created_at          TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at          TIMESTAMP,
    created_by          VARCHAR(50),
    updated_by          VARCHAR(50),

    CONSTRAINT uk_course_lesson_progress_enrollment_lesson UNIQUE (enrollment_id, lesson_id)
);

CREATE INDEX IF NOT EXISTS idx_courses_certification ON courses (certification_id);
CREATE INDEX IF NOT EXISTS idx_courses_published ON courses (is_published);
CREATE INDEX IF NOT EXISTS idx_course_modules_course_order ON course_modules (course_id, order_index);
CREATE INDEX IF NOT EXISTS idx_course_lessons_module_order ON course_lessons (module_id, order_index);
CREATE INDEX IF NOT EXISTS idx_course_enrollments_user ON course_enrollments (user_id);
CREATE INDEX IF NOT EXISTS idx_course_enrollments_course ON course_enrollments (course_id);
CREATE INDEX IF NOT EXISTS idx_course_lesson_progress_enrollment ON course_lesson_progress (enrollment_id);
CREATE INDEX IF NOT EXISTS idx_course_lesson_progress_lesson ON course_lesson_progress (lesson_id);

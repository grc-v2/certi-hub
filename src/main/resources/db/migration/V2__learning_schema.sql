-- Learning schema: certifications, exams, questions, mock-exam sessions and
-- results. Reconstructed from the JPA entities in
-- com.certimaster.learning.domain.entity so the schema is fully reproducible
-- from the repository (the original schema was migrated from the legacy
-- database). Legacy outbox/inbox tables were intentionally not copied; Spring
-- Modulith manages event publication through its Event Publication Registry
-- (see V1). Existing databases are baselined at version 1, so this script only
-- runs against a fresh database; statements are idempotent regardless.
--
-- Convention: user_id columns are plain BIGINT (no cross-module FK to the
-- identity schema), matching the engagement/productivity migrations.

-- Certification catalog -------------------------------------------------------

CREATE TABLE IF NOT EXISTS certifications (
    id                          BIGSERIAL PRIMARY KEY,
    name                        VARCHAR(255) NOT NULL,
    code                        VARCHAR(255) NOT NULL,
    slug                        VARCHAR(255) NOT NULL,
    tagline                     VARCHAR(500),
    provider                    VARCHAR(100),
    provider_name               VARCHAR(100),
    provider_logo               VARCHAR(500),
    track_name                  VARCHAR(100),
    description                 TEXT,
    overview                    TEXT,
    level                       VARCHAR(50),
    difficulty_level            VARCHAR(30),
    level_order                 INTEGER,
    duration_minutes            INTEGER,
    passing_score               NUMERIC(38, 2),
    passing_score_format        VARCHAR(50),
    total_questions             INTEGER,
    question_format             JSONB,
    exam_language               JSONB,
    price                       NUMERIC(38, 2),
    exam_price_usd              NUMERIC(38, 2),
    exam_price_details          JSONB,
    delivery_method             JSONB,
    scheduling_url              VARCHAR(500),
    prerequisites_description   TEXT,
    recommended_experience      JSONB,
    market_value_description    TEXT,
    target_audience             TEXT,
    validity_period_months      INTEGER,
    recertification_info        TEXT,
    version                     VARCHAR(50),
    version_release_date        DATE,
    version_retirement_date     DATE,
    average_prep_weeks          INTEGER,
    published_at                TIMESTAMP,
    status                      VARCHAR(20),
    resources                   JSONB,
    prerequisites               JSONB,

    created_at                  TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at                  TIMESTAMP,
    created_by                  VARCHAR(50),
    updated_by                  VARCHAR(50),

    CONSTRAINT uk_certifications_code UNIQUE (code),
    CONSTRAINT uk_certifications_slug UNIQUE (slug)
);

CREATE TABLE IF NOT EXISTS certification_domains (
    id                  BIGSERIAL PRIMARY KEY,
    certification_id    BIGINT NOT NULL REFERENCES certifications(id),
    name                VARCHAR(255) NOT NULL,
    code                VARCHAR(50),
    description         TEXT,
    weight_percentage   NUMERIC(38, 2),
    display_order       INTEGER,
    parent_domain_id    BIGINT REFERENCES certification_domains(id),
    status              VARCHAR(20),

    created_at          TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at          TIMESTAMP,
    created_by          VARCHAR(50),
    updated_by          VARCHAR(50)
);

-- Exams and questions ---------------------------------------------------------

CREATE TABLE IF NOT EXISTS exams (
    id                  BIGSERIAL PRIMARY KEY,
    certification_id    BIGINT NOT NULL REFERENCES certifications(id),
    title               VARCHAR(255),
    type                VARCHAR(50),
    description         TEXT,
    duration_minutes    INTEGER,
    total_questions     INTEGER,
    passing_score       INTEGER,
    status              VARCHAR(20),

    created_at          TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at          TIMESTAMP,
    created_by          VARCHAR(50),
    updated_by          VARCHAR(50)
);

CREATE TABLE IF NOT EXISTS questions (
    id                          BIGSERIAL PRIMARY KEY,
    domain_id                   BIGINT NOT NULL REFERENCES certification_domains(id),
    type                        VARCHAR(50) NOT NULL,
    content                     TEXT NOT NULL,
    explanation                 TEXT,
    difficulty                  VARCHAR(20),
    points                      INTEGER,
    time_limit_seconds          INTEGER,
    reference_url               VARCHAR(500),
    question_code               VARCHAR(50),
    scenario                    TEXT,
    question_media              JSONB,
    wrong_answer_explanations   JSONB,
    reference_urls              JSONB,
    estimated_time_seconds      INTEGER,
    status                      VARCHAR(20),
    review_notes                TEXT,
    reviewed_by                 VARCHAR(50),
    published_at                TIMESTAMP,

    created_at                  TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at                  TIMESTAMP,
    created_by                  VARCHAR(50),
    updated_by                  VARCHAR(50),

    CONSTRAINT uk_questions_question_code UNIQUE (question_code)
);

CREATE TABLE IF NOT EXISTS question_options (
    id                  BIGSERIAL PRIMARY KEY,
    question_id         BIGINT NOT NULL REFERENCES questions(id),
    content             TEXT NOT NULL,
    is_correct          BOOLEAN,
    order_index         INTEGER,

    created_at          TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at          TIMESTAMP,
    created_by          VARCHAR(50),
    updated_by          VARCHAR(50)
);

CREATE TABLE IF NOT EXISTS exam_questions (
    id                  BIGSERIAL PRIMARY KEY,
    exam_id             BIGINT NOT NULL REFERENCES exams(id),
    question_id         BIGINT NOT NULL REFERENCES questions(id),
    order_index         INTEGER,

    created_at          TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at          TIMESTAMP,
    created_by          VARCHAR(50),
    updated_by          VARCHAR(50)
);

-- Question tagging ------------------------------------------------------------

CREATE TABLE IF NOT EXISTS tags (
    id                  BIGSERIAL PRIMARY KEY,
    name                VARCHAR(100) NOT NULL,
    status              VARCHAR(20),

    created_at          TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at          TIMESTAMP,
    created_by          VARCHAR(50),
    updated_by          VARCHAR(50),

    CONSTRAINT uk_tags_name UNIQUE (name)
);

CREATE TABLE IF NOT EXISTS question_tags (
    id                  BIGSERIAL PRIMARY KEY,
    question_id         BIGINT NOT NULL REFERENCES questions(id),
    tag_id              BIGINT NOT NULL REFERENCES tags(id),

    created_at          TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at          TIMESTAMP,
    created_by          VARCHAR(50),
    updated_by          VARCHAR(50)
);

-- Question banks --------------------------------------------------------------

CREATE TABLE IF NOT EXISTS question_banks (
    id                  BIGSERIAL PRIMARY KEY,
    certification_id    BIGINT NOT NULL,
    name                VARCHAR(255) NOT NULL,
    description         TEXT,
    status              VARCHAR(20),

    created_at          TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at          TIMESTAMP,
    created_by          VARCHAR(50),
    updated_by          VARCHAR(50)
);

CREATE TABLE IF NOT EXISTS question_bank_items (
    id                  BIGSERIAL PRIMARY KEY,
    question_bank_id    BIGINT NOT NULL REFERENCES question_banks(id),
    question_id         BIGINT NOT NULL REFERENCES questions(id),
    position            INTEGER NOT NULL,

    created_at          TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at          TIMESTAMP,
    created_by          VARCHAR(50),
    updated_by          VARCHAR(50)
);

-- Exam sessions ---------------------------------------------------------------

CREATE TABLE IF NOT EXISTS user_exam_sessions (
    id                  BIGSERIAL PRIMARY KEY,
    user_id             BIGINT NOT NULL,
    exam_id             BIGINT REFERENCES exams(id),
    certification_id    BIGINT REFERENCES certifications(id),
    start_time          TIMESTAMP NOT NULL,
    end_time            TIMESTAMP,
    status              VARCHAR(20),
    mode                VARCHAR(20) NOT NULL,
    exam_title          VARCHAR(500),
    total_questions     INTEGER NOT NULL,
    duration_minutes    INTEGER,
    answered_count      INTEGER,
    correct_count       INTEGER,
    wrong_count         INTEGER,
    unanswered_count    INTEGER,
    flagged_count       INTEGER,
    time_spent_seconds  INTEGER,
    score               NUMERIC(10, 2),
    score_percentage    NUMERIC(5, 2),
    passed              BOOLEAN,
    domain_scores       JSONB,
    question_bank_id    BIGINT,
    domain_filter       JSONB,
    difficulty_filter   JSONB,

    created_at          TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at          TIMESTAMP,
    created_by          VARCHAR(50),
    updated_by          VARCHAR(50)
);

CREATE TABLE IF NOT EXISTS user_answers (
    id                  BIGSERIAL PRIMARY KEY,
    session_id          BIGINT NOT NULL REFERENCES user_exam_sessions(id),
    question_id         BIGINT NOT NULL REFERENCES questions(id),
    selected_option_ids BIGINT[],
    correct_option_ids  BIGINT[],
    is_correct          BOOLEAN,
    is_flagged          BOOLEAN,
    time_spent_seconds  INTEGER,
    answered_at         TIMESTAMP,
    question_order      INTEGER,
    option_order_map    JSONB,

    created_at          TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at          TIMESTAMP,
    created_by          VARCHAR(50),
    updated_by          VARCHAR(50)
);

-- Exam results ----------------------------------------------------------------

CREATE TABLE IF NOT EXISTS exam_results (
    id                  BIGSERIAL PRIMARY KEY,
    session_id          BIGINT NOT NULL,
    user_id             BIGINT NOT NULL,
    exam_id             BIGINT,
    certification_id    BIGINT NOT NULL,
    total_questions     INTEGER NOT NULL,
    correct_answers     INTEGER NOT NULL,
    wrong_answers       INTEGER NOT NULL,
    unanswered          INTEGER NOT NULL,
    score               NUMERIC(5, 2) NOT NULL,
    percentage          NUMERIC(5, 2) NOT NULL,
    pass_status         VARCHAR(20) NOT NULL,
    passing_score       INTEGER NOT NULL,
    mode                VARCHAR(20),
    time_taken_seconds  INTEGER,
    completed_at        TIMESTAMP NOT NULL,

    created_at          TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at          TIMESTAMP,
    created_by          VARCHAR(50),
    updated_by          VARCHAR(50)
);

CREATE TABLE IF NOT EXISTS topic_performances (
    id                  BIGSERIAL PRIMARY KEY,
    exam_result_id      BIGINT NOT NULL REFERENCES exam_results(id),
    topic_id            BIGINT NOT NULL,
    topic_name          VARCHAR(255) NOT NULL,
    weight              NUMERIC(5, 2),
    total_questions     INTEGER NOT NULL,
    correct_answers     INTEGER NOT NULL,
    wrong_answers       INTEGER NOT NULL,
    percentage          NUMERIC(5, 2),

    created_at          TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at          TIMESTAMP,
    created_by          VARCHAR(50),
    updated_by          VARCHAR(50)
);

CREATE TABLE IF NOT EXISTS question_results (
    id                  BIGSERIAL PRIMARY KEY,
    result_id           BIGINT REFERENCES exam_results(id),
    question_id         BIGINT NOT NULL,
    topic_id            BIGINT,
    topic_name          VARCHAR(255),
    difficulty          VARCHAR(20),
    question_type       VARCHAR(50),
    question_content    TEXT,
    user_answer_ids     BIGINT[],
    correct_answer_ids  BIGINT[],
    is_correct          BOOLEAN,
    time_spent_seconds  INTEGER,
    explanation         TEXT,
    reference_links     TEXT,
    was_flagged         BOOLEAN,
    was_reviewed        BOOLEAN,

    created_at          TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at          TIMESTAMP,
    created_by          VARCHAR(50),
    updated_by          VARCHAR(50)
);

-- Indexes ---------------------------------------------------------------------

CREATE INDEX IF NOT EXISTS idx_certification_domains_certification ON certification_domains (certification_id);
CREATE INDEX IF NOT EXISTS idx_certification_domains_parent ON certification_domains (parent_domain_id);
CREATE INDEX IF NOT EXISTS idx_exams_certification ON exams (certification_id);
CREATE INDEX IF NOT EXISTS idx_questions_domain ON questions (domain_id);
CREATE INDEX IF NOT EXISTS idx_question_options_question ON question_options (question_id);
CREATE INDEX IF NOT EXISTS idx_exam_questions_exam ON exam_questions (exam_id);
CREATE INDEX IF NOT EXISTS idx_exam_questions_question ON exam_questions (question_id);
CREATE INDEX IF NOT EXISTS idx_question_tags_question ON question_tags (question_id);
CREATE INDEX IF NOT EXISTS idx_question_tags_tag ON question_tags (tag_id);
CREATE INDEX IF NOT EXISTS idx_question_banks_certification ON question_banks (certification_id);
CREATE INDEX IF NOT EXISTS idx_question_bank_items_bank ON question_bank_items (question_bank_id);
CREATE INDEX IF NOT EXISTS idx_question_bank_items_question ON question_bank_items (question_id);
CREATE INDEX IF NOT EXISTS idx_user_exam_sessions_user ON user_exam_sessions (user_id);
CREATE INDEX IF NOT EXISTS idx_user_exam_sessions_user_status ON user_exam_sessions (user_id, status);
CREATE INDEX IF NOT EXISTS idx_user_answers_session ON user_answers (session_id);
CREATE INDEX IF NOT EXISTS idx_user_answers_question ON user_answers (question_id);
CREATE INDEX IF NOT EXISTS idx_exam_results_user ON exam_results (user_id);
CREATE INDEX IF NOT EXISTS idx_exam_results_session ON exam_results (session_id);
CREATE INDEX IF NOT EXISTS idx_topic_performances_result ON topic_performances (exam_result_id);
CREATE INDEX IF NOT EXISTS idx_question_results_result ON question_results (result_id);

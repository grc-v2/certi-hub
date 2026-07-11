-- Link a tracker resource to a certification the user is studying for, so the
-- certification detail page can show related resources and the Resource Hub can
-- filter by certification. certification_id is a plain BIGINT (no cross-module
-- FK to learning's certifications table), following the same convention as
-- user_id (see V5/V9/V10); validity is enforced in the application layer via
-- the learning module's public lookup API.

ALTER TABLE tracker_resources ADD COLUMN IF NOT EXISTS certification_id BIGINT;

CREATE INDEX IF NOT EXISTS idx_tracker_resources_certification ON tracker_resources (certification_id);

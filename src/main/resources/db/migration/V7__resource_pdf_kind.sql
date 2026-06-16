-- Allow the PDF resource kind (uploaded ebooks/files) in tracker_resources.
-- Recreates the kind CHECK constraint from V5 to add 'PDF'.

ALTER TABLE tracker_resources DROP CONSTRAINT IF EXISTS chk_tracker_resources_kind;
ALTER TABLE tracker_resources ADD CONSTRAINT chk_tracker_resources_kind
    CHECK (kind IN ('YOUTUBE', 'GITHUB', 'BLOG', 'NOTE', 'PDF'));

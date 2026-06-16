-- Drop the Course/LMS schema introduced in V4.
--
-- The Course/LMS feature is removed as part of refocusing the platform on its
-- four core goals (personal tracker, mock exam, centralized resources, blog).
-- Tables are dropped in foreign-key dependency order (children first).

DROP TABLE IF EXISTS course_lesson_progress;
DROP TABLE IF EXISTS course_enrollments;
DROP TABLE IF EXISTS course_lessons;
DROP TABLE IF EXISTS course_modules;
DROP TABLE IF EXISTS courses;

-- Manual cleanup script for legacy/unused tables.
--
-- This script intentionally lives outside db/migration so Flyway will not run it
-- automatically. Review the table list against the target environment before
-- execution.
--
-- It does not use CASCADE. If any remaining table still depends on one of these
-- tables, PostgreSQL will fail the DROP instead of removing additional objects.

BEGIN;

DROP TABLE IF EXISTS
    public.certification_curricula,
    public.comment_reactions,
    public.comments,
    public.curriculum_lessons,
    public.curriculum_modules,
    public.domain_lesson_templates,
    public.lesson_resources,
    public.notification_templates,
    public.notifications,
    public.performance_histories,
    public.progress_alert_rules,
    public.study_plan_check_ins,
    public.study_plan_daily_tasks,
    public.study_plan_domain_allocations,
    public.study_plan_lessons,
    public.study_plan_milestones,
    public.study_plan_weeks,
    public.study_plans,
    public.user_activity_log,
    public.user_domain_progress_history,
    public.user_notification_preferences,
    public.user_progress,
    public.user_progress_snapshots,
    public.user_streaks;

COMMIT;

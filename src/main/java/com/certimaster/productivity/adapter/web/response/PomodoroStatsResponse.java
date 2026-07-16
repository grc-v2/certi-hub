package com.certimaster.productivity.adapter.web.response;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;

/**
 * Focus summary for the Pomodoro widget: today's numbers, a rolling 7-day total, and a
 * per-day breakdown for a small bar chart. Only COMPLETED FOCUS sessions count — breaks
 * and abandoned runs are stored in the history but excluded from these figures.
 * Seconds are the planned countdown lengths ({@code durationSeconds}) summed up.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PomodoroStatsResponse {

    /** Completed FOCUS sessions started today. */
    private long todayFocusSessions;
    /** Total focus seconds today. */
    private long todayFocusSeconds;
    /** Completed FOCUS sessions in the last 7 days (today included). */
    private long weekFocusSessions;
    /** Total focus seconds in the last 7 days (today included). */
    private long weekFocusSeconds;
    /** Exactly 7 entries, oldest first, ending with today. Days without sessions are zero-filled. */
    private List<DayStat> days;

    /** Echo of the {@code taskId} filter; null when no task filter was given. */
    private Long taskId;
    /** All-time completed FOCUS sessions linked to {@code taskId}; null when no task filter was given. */
    private Long taskFocusSessions;

    public PomodoroStatsResponse() {
    }

    public long getTodayFocusSessions() { return todayFocusSessions; }
    public void setTodayFocusSessions(long todayFocusSessions) { this.todayFocusSessions = todayFocusSessions; }

    public long getTodayFocusSeconds() { return todayFocusSeconds; }
    public void setTodayFocusSeconds(long todayFocusSeconds) { this.todayFocusSeconds = todayFocusSeconds; }

    public long getWeekFocusSessions() { return weekFocusSessions; }
    public void setWeekFocusSessions(long weekFocusSessions) { this.weekFocusSessions = weekFocusSessions; }

    public long getWeekFocusSeconds() { return weekFocusSeconds; }
    public void setWeekFocusSeconds(long weekFocusSeconds) { this.weekFocusSeconds = weekFocusSeconds; }

    public List<DayStat> getDays() { return days; }
    public void setDays(List<DayStat> days) { this.days = days; }

    public Long getTaskId() { return taskId; }
    public void setTaskId(Long taskId) { this.taskId = taskId; }

    public Long getTaskFocusSessions() { return taskFocusSessions; }
    public void setTaskFocusSessions(Long taskFocusSessions) { this.taskFocusSessions = taskFocusSessions; }

    /** One bar of the 7-day chart. */
    public static class DayStat {

        /** ISO date, e.g. 2026-07-11. */
        private String date;
        private long focusSessions;
        private long focusSeconds;

        public DayStat() {
        }

        public DayStat(String date, long focusSessions, long focusSeconds) {
            this.date = date;
            this.focusSessions = focusSessions;
            this.focusSeconds = focusSeconds;
        }

        public String getDate() { return date; }
        public void setDate(String date) { this.date = date; }

        public long getFocusSessions() { return focusSessions; }
        public void setFocusSessions(long focusSessions) { this.focusSessions = focusSessions; }

        public long getFocusSeconds() { return focusSeconds; }
        public void setFocusSeconds(long focusSeconds) { this.focusSeconds = focusSeconds; }
    }
}

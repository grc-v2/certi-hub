package com.certimaster.productivity.adapter.web.request;

/**
 * Report the outcome of one client-side Pomodoro run.
 * kind: FOCUS | SHORT_BREAK | LONG_BREAK. startedAt: ISO local datetime
 * (e.g. 2026-07-11T09:30:00). completed defaults to true when omitted.
 */
public class RecordPomodoroSessionRequest {

    private String kind;
    private String startedAt;
    private Integer durationSeconds;
    private Boolean completed;
    private Long taskId;

    public RecordPomodoroSessionRequest() {
    }

    public String getKind() { return kind; }
    public void setKind(String kind) { this.kind = kind; }

    public String getStartedAt() { return startedAt; }
    public void setStartedAt(String startedAt) { this.startedAt = startedAt; }

    public Integer getDurationSeconds() { return durationSeconds; }
    public void setDurationSeconds(Integer durationSeconds) { this.durationSeconds = durationSeconds; }

    public Boolean getCompleted() { return completed; }
    public void setCompleted(Boolean completed) { this.completed = completed; }

    public Long getTaskId() { return taskId; }
    public void setTaskId(Long taskId) { this.taskId = taskId; }
}

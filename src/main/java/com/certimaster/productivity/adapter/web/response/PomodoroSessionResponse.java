package com.certimaster.productivity.adapter.web.response;

import com.certimaster.shared.dto.BaseResponse;

import java.time.LocalDateTime;

/** One recorded Pomodoro timer run (finished or abandoned), as stored in the history. */
public class PomodoroSessionResponse extends BaseResponse {

    private Long userId;
    /** Todo task the session was linked to, or null when it ran unlinked. */
    private Long taskId;
    /** FOCUS, SHORT_BREAK or LONG_BREAK. */
    private String kind;
    private LocalDateTime startedAt;
    private LocalDateTime endedAt;
    /** Planned countdown length in seconds. */
    private int durationSeconds;
    /** True = countdown ran to the end; false = abandoned early. */
    private boolean completed;

    public PomodoroSessionResponse() {
        super();
    }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public Long getTaskId() { return taskId; }
    public void setTaskId(Long taskId) { this.taskId = taskId; }

    public String getKind() { return kind; }
    public void setKind(String kind) { this.kind = kind; }

    public LocalDateTime getStartedAt() { return startedAt; }
    public void setStartedAt(LocalDateTime startedAt) { this.startedAt = startedAt; }

    public LocalDateTime getEndedAt() { return endedAt; }
    public void setEndedAt(LocalDateTime endedAt) { this.endedAt = endedAt; }

    public int getDurationSeconds() { return durationSeconds; }
    public void setDurationSeconds(int durationSeconds) { this.durationSeconds = durationSeconds; }

    public boolean isCompleted() { return completed; }
    public void setCompleted(boolean completed) { this.completed = completed; }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private Long id;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
        private String createdBy;
        private String updatedBy;
        private Long userId;
        private Long taskId;
        private String kind;
        private LocalDateTime startedAt;
        private LocalDateTime endedAt;
        private int durationSeconds;
        private boolean completed;

        public Builder id(Long v) { this.id = v; return this; }
        public Builder createdAt(LocalDateTime v) { this.createdAt = v; return this; }
        public Builder updatedAt(LocalDateTime v) { this.updatedAt = v; return this; }
        public Builder createdBy(String v) { this.createdBy = v; return this; }
        public Builder updatedBy(String v) { this.updatedBy = v; return this; }
        public Builder userId(Long v) { this.userId = v; return this; }
        public Builder taskId(Long v) { this.taskId = v; return this; }
        public Builder kind(String v) { this.kind = v; return this; }
        public Builder startedAt(LocalDateTime v) { this.startedAt = v; return this; }
        public Builder endedAt(LocalDateTime v) { this.endedAt = v; return this; }
        public Builder durationSeconds(int v) { this.durationSeconds = v; return this; }
        public Builder completed(boolean v) { this.completed = v; return this; }

        public PomodoroSessionResponse build() {
            PomodoroSessionResponse o = new PomodoroSessionResponse();
            o.setId(this.id);
            o.setCreatedAt(this.createdAt);
            o.setUpdatedAt(this.updatedAt);
            o.setCreatedBy(this.createdBy);
            o.setUpdatedBy(this.updatedBy);
            o.userId = this.userId;
            o.taskId = this.taskId;
            o.kind = this.kind;
            o.startedAt = this.startedAt;
            o.endedAt = this.endedAt;
            o.durationSeconds = this.durationSeconds;
            o.completed = this.completed;
            return o;
        }
    }
}

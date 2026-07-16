package com.certimaster.productivity.domain.entity;

import com.certimaster.shared.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;

import java.time.LocalDateTime;

/**
 * One finished (or abandoned) Pomodoro timer run. The timer itself runs client-side;
 * the client reports the outcome once the countdown ends or is given up, so this is a
 * write-once history row that feeds the stats endpoints. May optionally point at the
 * {@link TodoTask} the user was focusing on (plain id, validated for ownership when set).
 */
@Entity
@Table(name = "pomodoro_sessions")
public class PomodoroSession extends BaseEntity {

    @Column(name = "user_id", nullable = false)
    private Long userId;

    /** Optional link to the todo task worked on during this session. */
    @Column(name = "task_id")
    private Long taskId;

    @Enumerated(EnumType.STRING)
    @Column(name = "kind", nullable = false, length = 16)
    private PomodoroSessionKind kind = PomodoroSessionKind.FOCUS;

    @Column(name = "started_at", nullable = false)
    private LocalDateTime startedAt;

    @Column(name = "ended_at", nullable = false)
    private LocalDateTime endedAt;

    /** Planned countdown length in seconds (what the timer was set to). */
    @Column(name = "duration_seconds", nullable = false)
    private int durationSeconds;

    /** True when the countdown ran to the end, false when the user gave up early. */
    @Column(name = "completed", nullable = false)
    private boolean completed = true;

    public PomodoroSession() {
        super();
    }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public Long getTaskId() { return taskId; }
    public void setTaskId(Long taskId) { this.taskId = taskId; }

    public PomodoroSessionKind getKind() { return kind; }

    public LocalDateTime getStartedAt() { return startedAt; }

    public LocalDateTime getEndedAt() { return endedAt; }

    public int getDurationSeconds() { return durationSeconds; }

    public boolean isCompleted() { return completed; }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private Long userId;
        private Long taskId;
        private PomodoroSessionKind kind = PomodoroSessionKind.FOCUS;
        private LocalDateTime startedAt;
        private LocalDateTime endedAt;
        private int durationSeconds;
        private boolean completed = true;

        public Builder userId(Long v) { this.userId = v; return this; }
        public Builder taskId(Long v) { this.taskId = v; return this; }
        public Builder kind(PomodoroSessionKind v) { this.kind = v; return this; }
        public Builder startedAt(LocalDateTime v) { this.startedAt = v; return this; }
        public Builder endedAt(LocalDateTime v) { this.endedAt = v; return this; }
        public Builder durationSeconds(int v) { this.durationSeconds = v; return this; }
        public Builder completed(boolean v) { this.completed = v; return this; }

        public PomodoroSession build() {
            PomodoroSession o = new PomodoroSession();
            o.userId = this.userId;
            o.taskId = this.taskId;
            o.kind = this.kind != null ? this.kind : PomodoroSessionKind.FOCUS;
            o.startedAt = this.startedAt;
            o.endedAt = this.endedAt;
            o.durationSeconds = this.durationSeconds;
            o.completed = this.completed;
            return o;
        }
    }
}

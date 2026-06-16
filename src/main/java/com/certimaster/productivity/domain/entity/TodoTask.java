package com.certimaster.productivity.domain.entity;

import com.certimaster.shared.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * A single task on the kanban board. Owns the {@code doneAt} stamp: it is the sole
 * place that decides when the timestamp is set or cleared, so callers only ever
 * ask it to {@link #applyStatus(TaskStatus)} rather than touching the fields.
 */
@Entity
@Table(name = "todo_tasks")
public class TodoTask extends BaseEntity {

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "due_date")
    private LocalDate dueDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private TaskStatus status = TaskStatus.BACKLOG;

    @Column(name = "order_index", nullable = false)
    private int orderIndex = 0;

    @Column(name = "done_at")
    private LocalDateTime doneAt;

    public TodoTask() {
        super();
    }

    /**
     * Move the task to a new status, stamping {@code doneAt} when it enters DONE and
     * clearing it when it leaves. Re-entering DONE keeps the original completion time.
     */
    public void applyStatus(TaskStatus newStatus) {
        if (newStatus == null || newStatus == this.status) {
            return;
        }
        this.status = newStatus;
        if (newStatus == TaskStatus.DONE) {
            if (this.doneAt == null) {
                this.doneAt = LocalDateTime.now();
            }
        } else {
            this.doneAt = null;
        }
    }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public LocalDate getDueDate() { return dueDate; }
    public void setDueDate(LocalDate dueDate) { this.dueDate = dueDate; }

    public TaskStatus getStatus() { return status; }

    public int getOrderIndex() { return orderIndex; }
    public void setOrderIndex(int orderIndex) { this.orderIndex = orderIndex; }

    public LocalDateTime getDoneAt() { return doneAt; }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private Long userId;
        private String title;
        private String description;
        private LocalDate dueDate;
        private TaskStatus status = TaskStatus.BACKLOG;
        private int orderIndex = 0;

        public Builder userId(Long v) { this.userId = v; return this; }
        public Builder title(String v) { this.title = v; return this; }
        public Builder description(String v) { this.description = v; return this; }
        public Builder dueDate(LocalDate v) { this.dueDate = v; return this; }
        public Builder status(TaskStatus v) { this.status = v; return this; }
        public Builder orderIndex(int v) { this.orderIndex = v; return this; }

        public TodoTask build() {
            TodoTask o = new TodoTask();
            o.userId = this.userId;
            o.title = this.title;
            o.description = this.description;
            o.dueDate = this.dueDate;
            o.status = this.status != null ? this.status : TaskStatus.BACKLOG;
            o.orderIndex = this.orderIndex;
            if (o.status == TaskStatus.DONE) {
                o.doneAt = LocalDateTime.now();
            }
            return o;
        }
    }
}

package com.certimaster.productivity.adapter.web.response;

import com.certimaster.shared.dto.BaseResponse;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class TaskResponse extends BaseResponse {

    private Long userId;
    private String title;
    private String description;
    private LocalDate dueDate;
    private String status;
    private int orderIndex;
    private LocalDateTime doneAt;

    public TaskResponse() {
        super();
    }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public LocalDate getDueDate() { return dueDate; }
    public void setDueDate(LocalDate dueDate) { this.dueDate = dueDate; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public int getOrderIndex() { return orderIndex; }
    public void setOrderIndex(int orderIndex) { this.orderIndex = orderIndex; }

    public LocalDateTime getDoneAt() { return doneAt; }
    public void setDoneAt(LocalDateTime doneAt) { this.doneAt = doneAt; }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private Long id;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
        private String createdBy;
        private String updatedBy;
        private Long userId;
        private String title;
        private String description;
        private LocalDate dueDate;
        private String status;
        private int orderIndex;
        private LocalDateTime doneAt;

        public Builder id(Long v) { this.id = v; return this; }
        public Builder createdAt(LocalDateTime v) { this.createdAt = v; return this; }
        public Builder updatedAt(LocalDateTime v) { this.updatedAt = v; return this; }
        public Builder createdBy(String v) { this.createdBy = v; return this; }
        public Builder updatedBy(String v) { this.updatedBy = v; return this; }
        public Builder userId(Long v) { this.userId = v; return this; }
        public Builder title(String v) { this.title = v; return this; }
        public Builder description(String v) { this.description = v; return this; }
        public Builder dueDate(LocalDate v) { this.dueDate = v; return this; }
        public Builder status(String v) { this.status = v; return this; }
        public Builder orderIndex(int v) { this.orderIndex = v; return this; }
        public Builder doneAt(LocalDateTime v) { this.doneAt = v; return this; }

        public TaskResponse build() {
            TaskResponse o = new TaskResponse();
            o.setId(this.id);
            o.setCreatedAt(this.createdAt);
            o.setUpdatedAt(this.updatedAt);
            o.setCreatedBy(this.createdBy);
            o.setUpdatedBy(this.updatedBy);
            o.userId = this.userId;
            o.title = this.title;
            o.description = this.description;
            o.dueDate = this.dueDate;
            o.status = this.status;
            o.orderIndex = this.orderIndex;
            o.doneAt = this.doneAt;
            return o;
        }
    }
}

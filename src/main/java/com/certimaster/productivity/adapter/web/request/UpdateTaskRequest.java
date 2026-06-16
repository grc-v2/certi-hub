package com.certimaster.productivity.adapter.web.request;

/**
 * Partial task update. Any null field is left unchanged; {@code status} is one of
 * BACKLOG/TODO/DOING/DONE. To clear the due date, send {@code dueDate = ""}.
 */
public class UpdateTaskRequest {

    private String title;
    private String description;
    private String dueDate;
    private String status;
    private Integer orderIndex;

    public UpdateTaskRequest() {
    }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getDueDate() { return dueDate; }
    public void setDueDate(String dueDate) { this.dueDate = dueDate; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Integer getOrderIndex() { return orderIndex; }
    public void setOrderIndex(Integer orderIndex) { this.orderIndex = orderIndex; }
}

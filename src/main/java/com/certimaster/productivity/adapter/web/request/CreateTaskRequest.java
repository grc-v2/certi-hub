package com.certimaster.productivity.adapter.web.request;

/** Create a task. {@code dueDate} is an ISO date string (yyyy-MM-dd) or null. */
public class CreateTaskRequest {

    private String title;
    private String description;
    private String dueDate;

    public CreateTaskRequest() {
    }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getDueDate() { return dueDate; }
    public void setDueDate(String dueDate) { this.dueDate = dueDate; }
}

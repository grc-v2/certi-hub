package com.certimaster.learning.adapter.web.response;

public class CourseLessonSummaryResponse {

    private Long id;
    private String title;
    private Integer orderIndex;
    private Integer durationMinutes;
    private String type;
    private Boolean isCompleted;

    public CourseLessonSummaryResponse() {
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public Integer getOrderIndex() { return orderIndex; }
    public void setOrderIndex(Integer orderIndex) { this.orderIndex = orderIndex; }

    public Integer getDurationMinutes() { return durationMinutes; }
    public void setDurationMinutes(Integer durationMinutes) { this.durationMinutes = durationMinutes; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public Boolean getIsCompleted() { return isCompleted; }
    public void setIsCompleted(Boolean completed) { isCompleted = completed; }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private Long id;
        private String title;
        private Integer orderIndex;
        private Integer durationMinutes;
        private String type;
        private Boolean isCompleted;

        public Builder id(Long v) { this.id = v; return this; }
        public Builder title(String v) { this.title = v; return this; }
        public Builder orderIndex(Integer v) { this.orderIndex = v; return this; }
        public Builder durationMinutes(Integer v) { this.durationMinutes = v; return this; }
        public Builder type(String v) { this.type = v; return this; }
        public Builder isCompleted(Boolean v) { this.isCompleted = v; return this; }

        public CourseLessonSummaryResponse build() {
            CourseLessonSummaryResponse o = new CourseLessonSummaryResponse();
            o.id = this.id;
            o.title = this.title;
            o.orderIndex = this.orderIndex;
            o.durationMinutes = this.durationMinutes;
            o.type = this.type;
            o.isCompleted = this.isCompleted;
            return o;
        }
    }
}

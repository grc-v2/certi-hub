package com.certimaster.learning.adapter.web.response;

public class CourseLessonDetailResponse {

    private Long id;
    private Long courseId;
    private Long moduleId;
    private String title;
    private String description;
    private String content;
    private String videoUrl;
    private Integer orderIndex;
    private Integer durationMinutes;
    private String type;
    private Boolean isCompleted;
    private Long nextLessonId;
    private Long prevLessonId;

    public CourseLessonDetailResponse() {
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getCourseId() { return courseId; }
    public void setCourseId(Long courseId) { this.courseId = courseId; }

    public Long getModuleId() { return moduleId; }
    public void setModuleId(Long moduleId) { this.moduleId = moduleId; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public String getVideoUrl() { return videoUrl; }
    public void setVideoUrl(String videoUrl) { this.videoUrl = videoUrl; }

    public Integer getOrderIndex() { return orderIndex; }
    public void setOrderIndex(Integer orderIndex) { this.orderIndex = orderIndex; }

    public Integer getDurationMinutes() { return durationMinutes; }
    public void setDurationMinutes(Integer durationMinutes) { this.durationMinutes = durationMinutes; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public Boolean getIsCompleted() { return isCompleted; }
    public void setIsCompleted(Boolean completed) { isCompleted = completed; }

    public Long getNextLessonId() { return nextLessonId; }
    public void setNextLessonId(Long nextLessonId) { this.nextLessonId = nextLessonId; }

    public Long getPrevLessonId() { return prevLessonId; }
    public void setPrevLessonId(Long prevLessonId) { this.prevLessonId = prevLessonId; }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private Long id;
        private Long courseId;
        private Long moduleId;
        private String title;
        private String description;
        private String content;
        private String videoUrl;
        private Integer orderIndex;
        private Integer durationMinutes;
        private String type;
        private Boolean isCompleted;
        private Long nextLessonId;
        private Long prevLessonId;

        public Builder id(Long v) { this.id = v; return this; }
        public Builder courseId(Long v) { this.courseId = v; return this; }
        public Builder moduleId(Long v) { this.moduleId = v; return this; }
        public Builder title(String v) { this.title = v; return this; }
        public Builder description(String v) { this.description = v; return this; }
        public Builder content(String v) { this.content = v; return this; }
        public Builder videoUrl(String v) { this.videoUrl = v; return this; }
        public Builder orderIndex(Integer v) { this.orderIndex = v; return this; }
        public Builder durationMinutes(Integer v) { this.durationMinutes = v; return this; }
        public Builder type(String v) { this.type = v; return this; }
        public Builder isCompleted(Boolean v) { this.isCompleted = v; return this; }
        public Builder nextLessonId(Long v) { this.nextLessonId = v; return this; }
        public Builder prevLessonId(Long v) { this.prevLessonId = v; return this; }

        public CourseLessonDetailResponse build() {
            CourseLessonDetailResponse o = new CourseLessonDetailResponse();
            o.id = this.id;
            o.courseId = this.courseId;
            o.moduleId = this.moduleId;
            o.title = this.title;
            o.description = this.description;
            o.content = this.content;
            o.videoUrl = this.videoUrl;
            o.orderIndex = this.orderIndex;
            o.durationMinutes = this.durationMinutes;
            o.type = this.type;
            o.isCompleted = this.isCompleted;
            o.nextLessonId = this.nextLessonId;
            o.prevLessonId = this.prevLessonId;
            return o;
        }
    }
}

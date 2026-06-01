package com.certimaster.learning.adapter.web.response;

import java.util.ArrayList;
import java.util.List;

public class CourseModuleResponse {

    private Long id;
    private String title;
    private String description;
    private Integer orderIndex;
    private Integer lessonsCount;
    private List<CourseLessonSummaryResponse> lessons = new ArrayList<>();

    public CourseModuleResponse() {
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Integer getOrderIndex() { return orderIndex; }
    public void setOrderIndex(Integer orderIndex) { this.orderIndex = orderIndex; }

    public Integer getLessonsCount() { return lessonsCount; }
    public void setLessonsCount(Integer lessonsCount) { this.lessonsCount = lessonsCount; }

    public List<CourseLessonSummaryResponse> getLessons() { return lessons; }
    public void setLessons(List<CourseLessonSummaryResponse> lessons) { this.lessons = lessons; }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private Long id;
        private String title;
        private String description;
        private Integer orderIndex;
        private Integer lessonsCount;
        private List<CourseLessonSummaryResponse> lessons = new ArrayList<>();

        public Builder id(Long v) { this.id = v; return this; }
        public Builder title(String v) { this.title = v; return this; }
        public Builder description(String v) { this.description = v; return this; }
        public Builder orderIndex(Integer v) { this.orderIndex = v; return this; }
        public Builder lessonsCount(Integer v) { this.lessonsCount = v; return this; }
        public Builder lessons(List<CourseLessonSummaryResponse> v) { this.lessons = v; return this; }

        public CourseModuleResponse build() {
            CourseModuleResponse o = new CourseModuleResponse();
            o.id = this.id;
            o.title = this.title;
            o.description = this.description;
            o.orderIndex = this.orderIndex;
            o.lessonsCount = this.lessonsCount;
            o.lessons = this.lessons;
            return o;
        }
    }
}

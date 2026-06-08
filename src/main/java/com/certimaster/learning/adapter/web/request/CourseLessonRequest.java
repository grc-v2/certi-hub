package com.certimaster.learning.adapter.web.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class CourseLessonRequest {

    @NotBlank(message = "title is required")
    @Size(max = 255, message = "title must not exceed 255 characters")
    private String title;

    @Size(max = 5000, message = "description must not exceed 5000 characters")
    private String description;

    private String content;

    @Size(max = 500, message = "videoUrl must not exceed 500 characters")
    private String videoUrl;

    @Min(value = 0, message = "durationMinutes must be non-negative")
    @Max(value = 100000, message = "durationMinutes must not exceed 100000")
    private Integer durationMinutes;

    @Min(value = 1, message = "orderIndex must be at least 1")
    private Integer orderIndex;

    @NotBlank(message = "lessonType is required")
    @Pattern(regexp = "^(VIDEO|ARTICLE|QUIZ)$", message = "lessonType must be one of VIDEO, ARTICLE, QUIZ")
    private String lessonType;

    public CourseLessonRequest() {
    }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public String getVideoUrl() { return videoUrl; }
    public void setVideoUrl(String videoUrl) { this.videoUrl = videoUrl; }

    public Integer getDurationMinutes() { return durationMinutes; }
    public void setDurationMinutes(Integer durationMinutes) { this.durationMinutes = durationMinutes; }

    public Integer getOrderIndex() { return orderIndex; }
    public void setOrderIndex(Integer orderIndex) { this.orderIndex = orderIndex; }

    public String getLessonType() { return lessonType; }
    public void setLessonType(String lessonType) { this.lessonType = lessonType; }
}

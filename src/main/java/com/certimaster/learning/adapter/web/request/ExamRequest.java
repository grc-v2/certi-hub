package com.certimaster.learning.adapter.web.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class ExamRequest {

    @NotNull(message = "Certification ID is required")
    private Long certificationId;

    @NotBlank(message = "Title is required")
    @Size(max = 255, message = "Title must not exceed 255 characters")
    private String title;

    @NotBlank(message = "Type is required")
    @Pattern(regexp = "^(PRACTICE|MOCK|FINAL|DIAGNOSTIC|TOPIC_WISE)$",
             message = "Type must be one of: PRACTICE, MOCK, FINAL, DIAGNOSTIC, TOPIC_WISE")
    private String type;

    @Size(max = 5000, message = "Description must not exceed 5000 characters")
    private String description;

    @Min(value = 1, message = "Duration must be at least 1 minute")
    @Max(value = 600, message = "Duration must not exceed 600 minutes")
    private Integer durationMinutes;

    @Min(value = 1, message = "Total questions must be at least 1")
    @Max(value = 500, message = "Total questions must not exceed 500")
    private Integer totalQuestions;

    @Min(value = 0, message = "Passing score must be non-negative")
    @Max(value = 1000, message = "Passing score must not exceed 1000")
    private Integer passingScore;

    @Pattern(regexp = "^(ACTIVE|INACTIVE|DRAFT)$",
             message = "Status must be one of: ACTIVE, INACTIVE, DRAFT")
    private String status;

    public ExamRequest() {
    }

    public ExamRequest(Long certificationId, String title, String type, String description,
                       Integer durationMinutes, Integer totalQuestions, Integer passingScore, String status) {
        this.certificationId = certificationId;
        this.title = title;
        this.type = type;
        this.description = description;
        this.durationMinutes = durationMinutes;
        this.totalQuestions = totalQuestions;
        this.passingScore = passingScore;
        this.status = status;
    }

    public Long getCertificationId() {
        return certificationId;
    }

    public void setCertificationId(Long certificationId) {
        this.certificationId = certificationId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getDurationMinutes() {
        return durationMinutes;
    }

    public void setDurationMinutes(Integer durationMinutes) {
        this.durationMinutes = durationMinutes;
    }

    public Integer getTotalQuestions() {
        return totalQuestions;
    }

    public void setTotalQuestions(Integer totalQuestions) {
        this.totalQuestions = totalQuestions;
    }

    public Integer getPassingScore() {
        return passingScore;
    }

    public void setPassingScore(Integer passingScore) {
        this.passingScore = passingScore;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Long certificationId;
        private String title;
        private String type;
        private String description;
        private Integer durationMinutes;
        private Integer totalQuestions;
        private Integer passingScore;
        private String status;

        public Builder certificationId(Long v) { this.certificationId = v; return this; }
        public Builder title(String v) { this.title = v; return this; }
        public Builder type(String v) { this.type = v; return this; }
        public Builder description(String v) { this.description = v; return this; }
        public Builder durationMinutes(Integer v) { this.durationMinutes = v; return this; }
        public Builder totalQuestions(Integer v) { this.totalQuestions = v; return this; }
        public Builder passingScore(Integer v) { this.passingScore = v; return this; }
        public Builder status(String v) { this.status = v; return this; }

        public ExamRequest build() {
            ExamRequest o = new ExamRequest();
            o.certificationId = this.certificationId;
            o.title = this.title;
            o.type = this.type;
            o.description = this.description;
            o.durationMinutes = this.durationMinutes;
            o.totalQuestions = this.totalQuestions;
            o.passingScore = this.passingScore;
            o.status = this.status;
            return o;
        }
    }
}

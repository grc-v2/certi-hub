package com.certimaster.learning.adapter.web.response;

import com.certimaster.shared.dto.BaseResponse;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class CourseEnrollmentResponse extends BaseResponse {

    private Long courseId;
    private Long userId;
    private String status;
    private BigDecimal progressPercentage;
    private LocalDateTime lastAccessedAt;
    private LocalDateTime enrolledAt;
    private CourseResponse course;

    public CourseEnrollmentResponse() {
    }

    public Long getCourseId() { return courseId; }
    public void setCourseId(Long courseId) { this.courseId = courseId; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public BigDecimal getProgressPercentage() { return progressPercentage; }
    public void setProgressPercentage(BigDecimal progressPercentage) { this.progressPercentage = progressPercentage; }

    public LocalDateTime getLastAccessedAt() { return lastAccessedAt; }
    public void setLastAccessedAt(LocalDateTime lastAccessedAt) { this.lastAccessedAt = lastAccessedAt; }

    public LocalDateTime getEnrolledAt() { return enrolledAt; }
    public void setEnrolledAt(LocalDateTime enrolledAt) { this.enrolledAt = enrolledAt; }

    public CourseResponse getCourse() { return course; }
    public void setCourse(CourseResponse course) { this.course = course; }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private Long id;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
        private String createdBy;
        private String updatedBy;
        private Long courseId;
        private Long userId;
        private String status;
        private BigDecimal progressPercentage;
        private LocalDateTime lastAccessedAt;
        private LocalDateTime enrolledAt;
        private CourseResponse course;

        public Builder id(Long v) { this.id = v; return this; }
        public Builder createdAt(LocalDateTime v) { this.createdAt = v; return this; }
        public Builder updatedAt(LocalDateTime v) { this.updatedAt = v; return this; }
        public Builder createdBy(String v) { this.createdBy = v; return this; }
        public Builder updatedBy(String v) { this.updatedBy = v; return this; }
        public Builder courseId(Long v) { this.courseId = v; return this; }
        public Builder userId(Long v) { this.userId = v; return this; }
        public Builder status(String v) { this.status = v; return this; }
        public Builder progressPercentage(BigDecimal v) { this.progressPercentage = v; return this; }
        public Builder lastAccessedAt(LocalDateTime v) { this.lastAccessedAt = v; return this; }
        public Builder enrolledAt(LocalDateTime v) { this.enrolledAt = v; return this; }
        public Builder course(CourseResponse v) { this.course = v; return this; }

        public CourseEnrollmentResponse build() {
            CourseEnrollmentResponse o = new CourseEnrollmentResponse();
            o.setId(this.id);
            o.setCreatedAt(this.createdAt);
            o.setUpdatedAt(this.updatedAt);
            o.setCreatedBy(this.createdBy);
            o.setUpdatedBy(this.updatedBy);
            o.courseId = this.courseId;
            o.userId = this.userId;
            o.status = this.status;
            o.progressPercentage = this.progressPercentage;
            o.lastAccessedAt = this.lastAccessedAt;
            o.enrolledAt = this.enrolledAt;
            o.course = this.course;
            return o;
        }
    }
}

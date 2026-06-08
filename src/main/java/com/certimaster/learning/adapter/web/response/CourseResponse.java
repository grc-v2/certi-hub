package com.certimaster.learning.adapter.web.response;

import com.certimaster.shared.dto.BaseResponse;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class CourseResponse extends BaseResponse {

    private Long certificationId;
    private String certificationName;
    private String name;
    private String description;
    private String instructor;
    private String thumbnailUrl;
    private Integer totalModules;
    private Integer totalLessons;
    private Integer durationMinutes;
    private Integer enrolledCount;
    private Boolean published;
    private List<CourseModuleResponse> modules = new ArrayList<>();

    public CourseResponse() {
    }

    public Long getCertificationId() { return certificationId; }
    public void setCertificationId(Long certificationId) { this.certificationId = certificationId; }

    public String getCertificationName() { return certificationName; }
    public void setCertificationName(String certificationName) { this.certificationName = certificationName; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getInstructor() { return instructor; }
    public void setInstructor(String instructor) { this.instructor = instructor; }

    public String getThumbnailUrl() { return thumbnailUrl; }
    public void setThumbnailUrl(String thumbnailUrl) { this.thumbnailUrl = thumbnailUrl; }

    public Integer getTotalModules() { return totalModules; }
    public void setTotalModules(Integer totalModules) { this.totalModules = totalModules; }

    public Integer getTotalLessons() { return totalLessons; }
    public void setTotalLessons(Integer totalLessons) { this.totalLessons = totalLessons; }

    public Integer getDurationMinutes() { return durationMinutes; }
    public void setDurationMinutes(Integer durationMinutes) { this.durationMinutes = durationMinutes; }

    public Integer getEnrolledCount() { return enrolledCount; }
    public void setEnrolledCount(Integer enrolledCount) { this.enrolledCount = enrolledCount; }

    public Boolean getPublished() { return published; }
    public void setPublished(Boolean published) { this.published = published; }

    public List<CourseModuleResponse> getModules() { return modules; }
    public void setModules(List<CourseModuleResponse> modules) { this.modules = modules; }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private Long id;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
        private String createdBy;
        private String updatedBy;
        private Long certificationId;
        private String certificationName;
        private String name;
        private String description;
        private String instructor;
        private String thumbnailUrl;
        private Integer totalModules;
        private Integer totalLessons;
        private Integer durationMinutes;
        private Integer enrolledCount;
        private Boolean published;
        private List<CourseModuleResponse> modules = new ArrayList<>();

        public Builder id(Long v) { this.id = v; return this; }
        public Builder createdAt(LocalDateTime v) { this.createdAt = v; return this; }
        public Builder updatedAt(LocalDateTime v) { this.updatedAt = v; return this; }
        public Builder createdBy(String v) { this.createdBy = v; return this; }
        public Builder updatedBy(String v) { this.updatedBy = v; return this; }
        public Builder certificationId(Long v) { this.certificationId = v; return this; }
        public Builder certificationName(String v) { this.certificationName = v; return this; }
        public Builder name(String v) { this.name = v; return this; }
        public Builder description(String v) { this.description = v; return this; }
        public Builder instructor(String v) { this.instructor = v; return this; }
        public Builder thumbnailUrl(String v) { this.thumbnailUrl = v; return this; }
        public Builder totalModules(Integer v) { this.totalModules = v; return this; }
        public Builder totalLessons(Integer v) { this.totalLessons = v; return this; }
        public Builder durationMinutes(Integer v) { this.durationMinutes = v; return this; }
        public Builder enrolledCount(Integer v) { this.enrolledCount = v; return this; }
        public Builder published(Boolean v) { this.published = v; return this; }
        public Builder modules(List<CourseModuleResponse> v) { this.modules = v; return this; }

        public CourseResponse build() {
            CourseResponse o = new CourseResponse();
            o.setId(this.id);
            o.setCreatedAt(this.createdAt);
            o.setUpdatedAt(this.updatedAt);
            o.setCreatedBy(this.createdBy);
            o.setUpdatedBy(this.updatedBy);
            o.certificationId = this.certificationId;
            o.certificationName = this.certificationName;
            o.name = this.name;
            o.description = this.description;
            o.instructor = this.instructor;
            o.thumbnailUrl = this.thumbnailUrl;
            o.totalModules = this.totalModules;
            o.totalLessons = this.totalLessons;
            o.durationMinutes = this.durationMinutes;
            o.enrolledCount = this.enrolledCount;
            o.published = this.published;
            o.modules = this.modules;
            return o;
        }
    }
}

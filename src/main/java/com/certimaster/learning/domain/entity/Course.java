package com.certimaster.learning.domain.entity;

import com.certimaster.shared.entity.BaseEntity;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "courses")
public class Course extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "certification_id", nullable = false)
    private Certification certification;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "instructor")
    private String instructor;

    @Column(name = "thumbnail_url", length = 500)
    private String thumbnailUrl;

    @Column(name = "duration_minutes")
    private Integer durationMinutes;

    @Column(name = "enrolled_count")
    private Integer enrolledCount = 0;

    @Column(name = "is_published")
    private Boolean published = false;

    @OneToMany(mappedBy = "course", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private Set<CourseModule> modules = new HashSet<>();

    public Course() {
        super();
    }

    public Certification getCertification() { return certification; }
    public void setCertification(Certification certification) { this.certification = certification; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getInstructor() { return instructor; }
    public void setInstructor(String instructor) { this.instructor = instructor; }

    public String getThumbnailUrl() { return thumbnailUrl; }
    public void setThumbnailUrl(String thumbnailUrl) { this.thumbnailUrl = thumbnailUrl; }

    public Integer getDurationMinutes() { return durationMinutes; }
    public void setDurationMinutes(Integer durationMinutes) { this.durationMinutes = durationMinutes; }

    public Integer getEnrolledCount() { return enrolledCount; }
    public void setEnrolledCount(Integer enrolledCount) { this.enrolledCount = enrolledCount; }

    public Boolean getPublished() { return published; }
    public void setPublished(Boolean published) { this.published = published; }

    public Set<CourseModule> getModules() { return modules; }
    public void setModules(Set<CourseModule> modules) { this.modules = modules; }

    public void incrementEnrolledCount() {
        this.enrolledCount = (this.enrolledCount == null ? 0 : this.enrolledCount) + 1;
    }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private Certification certification;
        private String name;
        private String description;
        private String instructor;
        private String thumbnailUrl;
        private Integer durationMinutes;
        private Integer enrolledCount = 0;
        private Boolean published = false;
        private Set<CourseModule> modules = new HashSet<>();

        public Builder certification(Certification v) { this.certification = v; return this; }
        public Builder name(String v) { this.name = v; return this; }
        public Builder description(String v) { this.description = v; return this; }
        public Builder instructor(String v) { this.instructor = v; return this; }
        public Builder thumbnailUrl(String v) { this.thumbnailUrl = v; return this; }
        public Builder durationMinutes(Integer v) { this.durationMinutes = v; return this; }
        public Builder enrolledCount(Integer v) { this.enrolledCount = v; return this; }
        public Builder published(Boolean v) { this.published = v; return this; }
        public Builder modules(Set<CourseModule> v) { this.modules = v; return this; }

        public Course build() {
            Course o = new Course();
            o.certification = this.certification;
            o.name = this.name;
            o.description = this.description;
            o.instructor = this.instructor;
            o.thumbnailUrl = this.thumbnailUrl;
            o.durationMinutes = this.durationMinutes;
            o.enrolledCount = this.enrolledCount;
            o.published = this.published;
            o.modules = this.modules;
            return o;
        }
    }
}

package com.certimaster.learning.adapter.web.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class CourseRequest {

    @NotNull(message = "certificationId is required")
    private Long certificationId;

    @NotBlank(message = "name is required")
    @Size(max = 255, message = "name must not exceed 255 characters")
    private String name;

    @Size(max = 5000, message = "description must not exceed 5000 characters")
    private String description;

    @Size(max = 255, message = "instructor must not exceed 255 characters")
    private String instructor;

    @Size(max = 500, message = "thumbnailUrl must not exceed 500 characters")
    private String thumbnailUrl;

    @Min(value = 0, message = "durationMinutes must be non-negative")
    @Max(value = 100000, message = "durationMinutes must not exceed 100000")
    private Integer durationMinutes;

    public CourseRequest() {
    }

    public Long getCertificationId() { return certificationId; }
    public void setCertificationId(Long certificationId) { this.certificationId = certificationId; }

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
}

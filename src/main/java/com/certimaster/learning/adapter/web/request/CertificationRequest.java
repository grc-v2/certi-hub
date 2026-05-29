package com.certimaster.learning.adapter.web.request;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public class CertificationRequest {

    @NotBlank(message = "Name is required")
    @Size(max = 255, message = "Name must not exceed 255 characters")
    private String name;

    @NotBlank(message = "Code is required")
    @Size(max = 50, message = "Code must not exceed 50 characters")
    @Pattern(regexp = "^[A-Z0-9-]+$", message = "Code must contain only uppercase letters, numbers, and hyphens")
    private String code;

    @Size(max = 100, message = "Provider must not exceed 100 characters")
    private String provider;

    @Size(max = 5000, message = "Description must not exceed 5000 characters")
    private String description;

    @Pattern(regexp = "^(BEGINNER|INTERMEDIATE|ADVANCED|EXPERT)$", message = "Invalid level")
    private String level;

    @Min(value = 1, message = "Duration must be at least 1 minute")
    @Max(value = 600, message = "Duration must not exceed 600 minutes")
    private Integer durationMinutes;

    @DecimalMin(value = "0.0", message = "Passing score must be non-negative")
    @DecimalMax(value = "1000.0", message = "Passing score must not exceed 1000")
    private BigDecimal passingScore;

    @Min(value = 1, message = "Total questions must be at least 1")
    @Max(value = 500, message = "Total questions must not exceed 500")
    private Integer totalQuestions;

    @DecimalMin(value = "0.0", message = "Price must be non-negative")
    private BigDecimal price;

    @Pattern(regexp = "^(ACTIVE|INACTIVE|DRAFT)$", message = "Invalid status")
    private String status;

    public CertificationRequest() {
    }

    public CertificationRequest(String name, String code, String provider, String description, String level,
                                 Integer durationMinutes, BigDecimal passingScore, Integer totalQuestions,
                                 BigDecimal price, String status) {
        this.name = name;
        this.code = code;
        this.provider = provider;
        this.description = description;
        this.level = level;
        this.durationMinutes = durationMinutes;
        this.passingScore = passingScore;
        this.totalQuestions = totalQuestions;
        this.price = price;
        this.status = status;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getProvider() {
        return provider;
    }

    public void setProvider(String provider) {
        this.provider = provider;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public Integer getDurationMinutes() {
        return durationMinutes;
    }

    public void setDurationMinutes(Integer durationMinutes) {
        this.durationMinutes = durationMinutes;
    }

    public BigDecimal getPassingScore() {
        return passingScore;
    }

    public void setPassingScore(BigDecimal passingScore) {
        this.passingScore = passingScore;
    }

    public Integer getTotalQuestions() {
        return totalQuestions;
    }

    public void setTotalQuestions(Integer totalQuestions) {
        this.totalQuestions = totalQuestions;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
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
        private String name;
        private String code;
        private String provider;
        private String description;
        private String level;
        private Integer durationMinutes;
        private BigDecimal passingScore;
        private Integer totalQuestions;
        private BigDecimal price;
        private String status;

        public Builder name(String v) { this.name = v; return this; }
        public Builder code(String v) { this.code = v; return this; }
        public Builder provider(String v) { this.provider = v; return this; }
        public Builder description(String v) { this.description = v; return this; }
        public Builder level(String v) { this.level = v; return this; }
        public Builder durationMinutes(Integer v) { this.durationMinutes = v; return this; }
        public Builder passingScore(BigDecimal v) { this.passingScore = v; return this; }
        public Builder totalQuestions(Integer v) { this.totalQuestions = v; return this; }
        public Builder price(BigDecimal v) { this.price = v; return this; }
        public Builder status(String v) { this.status = v; return this; }

        public CertificationRequest build() {
            CertificationRequest o = new CertificationRequest();
            o.name = this.name;
            o.code = this.code;
            o.provider = this.provider;
            o.description = this.description;
            o.level = this.level;
            o.durationMinutes = this.durationMinutes;
            o.passingScore = this.passingScore;
            o.totalQuestions = this.totalQuestions;
            o.price = this.price;
            o.status = this.status;
            return o;
        }
    }
}

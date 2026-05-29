package com.certimaster.learning.adapter.web.response;

import com.certimaster.shared.dto.BaseResponse;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class CertificationResponse extends BaseResponse {

    private String name;
    private String code;
    private String slug;
    private String tagline;
    private String provider;
    private String level;
    private String difficultyLevel;
    private String description;
    private String overview;
    private Integer durationMinutes;
    private Integer totalQuestions;
    private BigDecimal passingScore;
    private BigDecimal price;
    private BigDecimal examPriceUsd;
    private Integer averagePrepWeeks;
    private String status;

    public CertificationResponse() {}

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }

    public String getSlug() { return slug; }
    public void setSlug(String slug) { this.slug = slug; }

    public String getTagline() { return tagline; }
    public void setTagline(String tagline) { this.tagline = tagline; }

    public String getProvider() { return provider; }
    public void setProvider(String provider) { this.provider = provider; }

    public String getLevel() { return level; }
    public void setLevel(String level) { this.level = level; }

    public String getDifficultyLevel() { return difficultyLevel; }
    public void setDifficultyLevel(String difficultyLevel) { this.difficultyLevel = difficultyLevel; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getOverview() { return overview; }
    public void setOverview(String overview) { this.overview = overview; }

    public Integer getDurationMinutes() { return durationMinutes; }
    public void setDurationMinutes(Integer durationMinutes) { this.durationMinutes = durationMinutes; }

    public Integer getTotalQuestions() { return totalQuestions; }
    public void setTotalQuestions(Integer totalQuestions) { this.totalQuestions = totalQuestions; }

    public BigDecimal getPassingScore() { return passingScore; }
    public void setPassingScore(BigDecimal passingScore) { this.passingScore = passingScore; }

    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }

    public BigDecimal getExamPriceUsd() { return examPriceUsd; }
    public void setExamPriceUsd(BigDecimal examPriceUsd) { this.examPriceUsd = examPriceUsd; }

    public Integer getAveragePrepWeeks() { return averagePrepWeeks; }
    public void setAveragePrepWeeks(Integer averagePrepWeeks) { this.averagePrepWeeks = averagePrepWeeks; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private Long id;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
        private String createdBy;
        private String updatedBy;
        private String name;
        private String code;
        private String slug;
        private String tagline;
        private String provider;
        private String level;
        private String difficultyLevel;
        private String description;
        private String overview;
        private Integer durationMinutes;
        private Integer totalQuestions;
        private BigDecimal passingScore;
        private BigDecimal price;
        private BigDecimal examPriceUsd;
        private Integer averagePrepWeeks;
        private String status;

        public Builder id(Long v) { this.id = v; return this; }
        public Builder createdAt(LocalDateTime v) { this.createdAt = v; return this; }
        public Builder updatedAt(LocalDateTime v) { this.updatedAt = v; return this; }
        public Builder createdBy(String v) { this.createdBy = v; return this; }
        public Builder updatedBy(String v) { this.updatedBy = v; return this; }
        public Builder name(String v) { this.name = v; return this; }
        public Builder code(String v) { this.code = v; return this; }
        public Builder slug(String v) { this.slug = v; return this; }
        public Builder tagline(String v) { this.tagline = v; return this; }
        public Builder provider(String v) { this.provider = v; return this; }
        public Builder level(String v) { this.level = v; return this; }
        public Builder difficultyLevel(String v) { this.difficultyLevel = v; return this; }
        public Builder description(String v) { this.description = v; return this; }
        public Builder overview(String v) { this.overview = v; return this; }
        public Builder durationMinutes(Integer v) { this.durationMinutes = v; return this; }
        public Builder totalQuestions(Integer v) { this.totalQuestions = v; return this; }
        public Builder passingScore(BigDecimal v) { this.passingScore = v; return this; }
        public Builder price(BigDecimal v) { this.price = v; return this; }
        public Builder examPriceUsd(BigDecimal v) { this.examPriceUsd = v; return this; }
        public Builder averagePrepWeeks(Integer v) { this.averagePrepWeeks = v; return this; }
        public Builder status(String v) { this.status = v; return this; }

        public CertificationResponse build() {
            CertificationResponse o = new CertificationResponse();
            o.setId(this.id);
            o.setCreatedAt(this.createdAt);
            o.setUpdatedAt(this.updatedAt);
            o.setCreatedBy(this.createdBy);
            o.setUpdatedBy(this.updatedBy);
            o.name = this.name;
            o.code = this.code;
            o.slug = this.slug;
            o.tagline = this.tagline;
            o.provider = this.provider;
            o.level = this.level;
            o.difficultyLevel = this.difficultyLevel;
            o.description = this.description;
            o.overview = this.overview;
            o.durationMinutes = this.durationMinutes;
            o.totalQuestions = this.totalQuestions;
            o.passingScore = this.passingScore;
            o.price = this.price;
            o.examPriceUsd = this.examPriceUsd;
            o.averagePrepWeeks = this.averagePrepWeeks;
            o.status = this.status;
            return o;
        }
    }
}

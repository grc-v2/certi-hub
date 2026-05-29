package com.certimaster.learning.domain.entity;

import com.certimaster.shared.entity.BaseEntity;
import com.certimaster.learning.domain.entity.Exam;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Entity
@Table(name = "certifications")
public class Certification extends BaseEntity {

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "code", nullable = false, unique = true)
    private String code;

    @Column(name = "slug", nullable = false, unique = true)
    private String slug;

    @Column(name = "tagline", length = 500)
    private String tagline;

    // Legacy field - kept for backward compatibility
    @Column(name = "provider", length = 100)
    private String provider;

    @Column(name = "provider_name", length = 100)
    private String providerName;

    @Column(name = "provider_logo", length = 500)
    private String providerLogo;

    @Column(name = "track_name", length = 100)
    private String trackName;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "overview", columnDefinition = "TEXT")
    private String overview;

    @Column(name = "level", length = 50)
    private String level;

    @Column(name = "difficulty_level", length = 30)
    private String difficultyLevel;

    @Column(name = "level_order")
    private Integer levelOrder;

    @Column(name = "duration_minutes")
    private Integer durationMinutes;

    @Column(name = "passing_score")
    private BigDecimal passingScore;

    @Column(name = "passing_score_format", length = 50)
    private String passingScoreFormat;

    @Column(name = "total_questions")
    private Integer totalQuestions;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "question_format", columnDefinition = "jsonb")
    private List<String> questionFormat;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "exam_language", columnDefinition = "jsonb")
    private List<String> examLanguage;

    @Column(name = "price")
    private BigDecimal price;

    @Column(name = "exam_price_usd")
    private BigDecimal examPriceUsd;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "exam_price_details", columnDefinition = "jsonb")
    private Map<String, Object> examPriceDetails;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "delivery_method", columnDefinition = "jsonb")
    private List<String> deliveryMethod;

    @Column(name = "scheduling_url", length = 500)
    private String schedulingUrl;

    @Column(name = "prerequisites_description", columnDefinition = "TEXT")
    private String prerequisitesDescription;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "recommended_experience", columnDefinition = "jsonb")
    private List<String> recommendedExperience;

    @Column(name = "market_value_description", columnDefinition = "TEXT")
    private String marketValueDescription;

    @Column(name = "target_audience", columnDefinition = "TEXT")
    private String targetAudience;

    @Column(name = "validity_period_months")
    private Integer validityPeriodMonths;

    @Column(name = "recertification_info", columnDefinition = "TEXT")
    private String recertificationInfo;

    @Column(name = "version", length = 50)
    private String version;

    @Column(name = "version_release_date")
    private LocalDate versionReleaseDate;

    @Column(name = "version_retirement_date")
    private LocalDate versionRetirementDate;

    @Column(name = "average_prep_weeks")
    private Integer averagePrepWeeks;

    @Column(name = "published_at")
    private LocalDateTime publishedAt;

    @Column(name = "status", length = 20)
    private String status;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "resources", columnDefinition = "jsonb")
    private List<Map<String, Object>> certResources = new ArrayList<>();

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "prerequisites", columnDefinition = "jsonb")
    private List<Map<String, Object>> prerequisites = new ArrayList<>();

    @OneToMany(mappedBy = "certification", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<CertificationDomain> domains = new HashSet<>();

    @OneToMany(mappedBy = "certification", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<Exam> exams = new HashSet<>();

    public Certification() {
        super();
    }

    // Getters and setters
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

    public String getProviderName() { return providerName; }
    public void setProviderName(String providerName) { this.providerName = providerName; }

    public String getProviderLogo() { return providerLogo; }
    public void setProviderLogo(String providerLogo) { this.providerLogo = providerLogo; }

    public String getTrackName() { return trackName; }
    public void setTrackName(String trackName) { this.trackName = trackName; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getOverview() { return overview; }
    public void setOverview(String overview) { this.overview = overview; }

    public String getLevel() { return level; }
    public void setLevel(String level) { this.level = level; }

    public String getDifficultyLevel() { return difficultyLevel; }
    public void setDifficultyLevel(String difficultyLevel) { this.difficultyLevel = difficultyLevel; }

    public Integer getLevelOrder() { return levelOrder; }
    public void setLevelOrder(Integer levelOrder) { this.levelOrder = levelOrder; }

    public Integer getDurationMinutes() { return durationMinutes; }
    public void setDurationMinutes(Integer durationMinutes) { this.durationMinutes = durationMinutes; }

    public BigDecimal getPassingScore() { return passingScore; }
    public void setPassingScore(BigDecimal passingScore) { this.passingScore = passingScore; }

    public String getPassingScoreFormat() { return passingScoreFormat; }
    public void setPassingScoreFormat(String passingScoreFormat) { this.passingScoreFormat = passingScoreFormat; }

    public Integer getTotalQuestions() { return totalQuestions; }
    public void setTotalQuestions(Integer totalQuestions) { this.totalQuestions = totalQuestions; }

    public List<String> getQuestionFormat() { return questionFormat; }
    public void setQuestionFormat(List<String> questionFormat) { this.questionFormat = questionFormat; }

    public List<String> getExamLanguage() { return examLanguage; }
    public void setExamLanguage(List<String> examLanguage) { this.examLanguage = examLanguage; }

    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }

    public BigDecimal getExamPriceUsd() { return examPriceUsd; }
    public void setExamPriceUsd(BigDecimal examPriceUsd) { this.examPriceUsd = examPriceUsd; }

    public Map<String, Object> getExamPriceDetails() { return examPriceDetails; }
    public void setExamPriceDetails(Map<String, Object> examPriceDetails) { this.examPriceDetails = examPriceDetails; }

    public List<String> getDeliveryMethod() { return deliveryMethod; }
    public void setDeliveryMethod(List<String> deliveryMethod) { this.deliveryMethod = deliveryMethod; }

    public String getSchedulingUrl() { return schedulingUrl; }
    public void setSchedulingUrl(String schedulingUrl) { this.schedulingUrl = schedulingUrl; }

    public String getPrerequisitesDescription() { return prerequisitesDescription; }
    public void setPrerequisitesDescription(String prerequisitesDescription) { this.prerequisitesDescription = prerequisitesDescription; }

    public List<String> getRecommendedExperience() { return recommendedExperience; }
    public void setRecommendedExperience(List<String> recommendedExperience) { this.recommendedExperience = recommendedExperience; }

    public String getMarketValueDescription() { return marketValueDescription; }
    public void setMarketValueDescription(String marketValueDescription) { this.marketValueDescription = marketValueDescription; }

    public String getTargetAudience() { return targetAudience; }
    public void setTargetAudience(String targetAudience) { this.targetAudience = targetAudience; }

    public Integer getValidityPeriodMonths() { return validityPeriodMonths; }
    public void setValidityPeriodMonths(Integer validityPeriodMonths) { this.validityPeriodMonths = validityPeriodMonths; }

    public String getRecertificationInfo() { return recertificationInfo; }
    public void setRecertificationInfo(String recertificationInfo) { this.recertificationInfo = recertificationInfo; }

    public String getVersion() { return version; }
    public void setVersion(String version) { this.version = version; }

    public LocalDate getVersionReleaseDate() { return versionReleaseDate; }
    public void setVersionReleaseDate(LocalDate versionReleaseDate) { this.versionReleaseDate = versionReleaseDate; }

    public LocalDate getVersionRetirementDate() { return versionRetirementDate; }
    public void setVersionRetirementDate(LocalDate versionRetirementDate) { this.versionRetirementDate = versionRetirementDate; }

    public Integer getAveragePrepWeeks() { return averagePrepWeeks; }
    public void setAveragePrepWeeks(Integer averagePrepWeeks) { this.averagePrepWeeks = averagePrepWeeks; }

    public LocalDateTime getPublishedAt() { return publishedAt; }
    public void setPublishedAt(LocalDateTime publishedAt) { this.publishedAt = publishedAt; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public List<Map<String, Object>> getCertResources() { return certResources; }
    public void setCertResources(List<Map<String, Object>> certResources) { this.certResources = certResources; }

    public List<Map<String, Object>> getPrerequisites() { return prerequisites; }
    public void setPrerequisites(List<Map<String, Object>> prerequisites) { this.prerequisites = prerequisites; }

    public Set<CertificationDomain> getDomains() { return domains; }
    public void setDomains(Set<CertificationDomain> domains) { this.domains = domains; }

    public Set<Exam> getExams() { return exams; }
    public void setExams(Set<Exam> exams) { this.exams = exams; }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private String name;
        private String code;
        private String slug;
        private String tagline;
        private String provider;
        private String providerName;
        private String providerLogo;
        private String trackName;
        private String description;
        private String overview;
        private String level;
        private String difficultyLevel;
        private Integer levelOrder;
        private Integer durationMinutes;
        private BigDecimal passingScore;
        private Integer totalQuestions;
        private BigDecimal price;
        private BigDecimal examPriceUsd;
        private String status;
        private Integer averagePrepWeeks;
        private Set<CertificationDomain> domains = new HashSet<>();
        private Set<Exam> exams = new HashSet<>();

        public Builder name(String v) { this.name = v; return this; }
        public Builder code(String v) { this.code = v; return this; }
        public Builder slug(String v) { this.slug = v; return this; }
        public Builder tagline(String v) { this.tagline = v; return this; }
        public Builder provider(String v) { this.provider = v; return this; }
        public Builder providerName(String v) { this.providerName = v; return this; }
        public Builder providerLogo(String v) { this.providerLogo = v; return this; }
        public Builder trackName(String v) { this.trackName = v; return this; }
        public Builder description(String v) { this.description = v; return this; }
        public Builder overview(String v) { this.overview = v; return this; }
        public Builder level(String v) { this.level = v; return this; }
        public Builder difficultyLevel(String v) { this.difficultyLevel = v; return this; }
        public Builder levelOrder(Integer v) { this.levelOrder = v; return this; }
        public Builder durationMinutes(Integer v) { this.durationMinutes = v; return this; }
        public Builder passingScore(BigDecimal v) { this.passingScore = v; return this; }
        public Builder totalQuestions(Integer v) { this.totalQuestions = v; return this; }
        public Builder price(BigDecimal v) { this.price = v; return this; }
        public Builder examPriceUsd(BigDecimal v) { this.examPriceUsd = v; return this; }
        public Builder status(String v) { this.status = v; return this; }
        public Builder averagePrepWeeks(Integer v) { this.averagePrepWeeks = v; return this; }
        public Builder domains(Set<CertificationDomain> v) { this.domains = v; return this; }
        public Builder exams(Set<Exam> v) { this.exams = v; return this; }

        public Certification build() {
            Certification o = new Certification();
            o.name = this.name;
            o.code = this.code;
            o.slug = this.slug;
            o.tagline = this.tagline;
            o.provider = this.provider;
            o.providerName = this.providerName;
            o.providerLogo = this.providerLogo;
            o.trackName = this.trackName;
            o.description = this.description;
            o.overview = this.overview;
            o.level = this.level;
            o.difficultyLevel = this.difficultyLevel;
            o.levelOrder = this.levelOrder;
            o.durationMinutes = this.durationMinutes;
            o.passingScore = this.passingScore;
            o.totalQuestions = this.totalQuestions;
            o.price = this.price;
            o.examPriceUsd = this.examPriceUsd;
            o.status = this.status;
            o.averagePrepWeeks = this.averagePrepWeeks;
            o.domains = this.domains;
            o.exams = this.exams;
            return o;
        }
    }
}

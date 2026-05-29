package com.certimaster.learning.adapter.web.response;

import com.certimaster.learning.adapter.web.response.ExamResponse;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class CertificationDetailResponse extends CertificationResponse {

    private List<CertificationDomainResponse> domains;
    private List<ExamResponse> exams;

    public CertificationDetailResponse() {}

    public List<CertificationDomainResponse> getDomains() { return domains; }
    public void setDomains(List<CertificationDomainResponse> domains) { this.domains = domains; }

    public List<ExamResponse> getExams() { return exams; }
    public void setExams(List<ExamResponse> exams) { this.exams = exams; }

    public static Builder detailBuilder() { return new Builder(); }

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
        private List<CertificationDomainResponse> domains;
        private List<ExamResponse> exams;

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
        public Builder domains(List<CertificationDomainResponse> v) { this.domains = v; return this; }
        public Builder exams(List<ExamResponse> v) { this.exams = v; return this; }

        public CertificationDetailResponse build() {
            CertificationDetailResponse o = new CertificationDetailResponse();
            o.setId(this.id);
            o.setCreatedAt(this.createdAt);
            o.setUpdatedAt(this.updatedAt);
            o.setCreatedBy(this.createdBy);
            o.setUpdatedBy(this.updatedBy);
            o.setName(this.name);
            o.setCode(this.code);
            o.setSlug(this.slug);
            o.setTagline(this.tagline);
            o.setProvider(this.provider);
            o.setLevel(this.level);
            o.setDifficultyLevel(this.difficultyLevel);
            o.setDescription(this.description);
            o.setOverview(this.overview);
            o.setDurationMinutes(this.durationMinutes);
            o.setTotalQuestions(this.totalQuestions);
            o.setPassingScore(this.passingScore);
            o.setPrice(this.price);
            o.setExamPriceUsd(this.examPriceUsd);
            o.setAveragePrepWeeks(this.averagePrepWeeks);
            o.setStatus(this.status);
            o.domains = this.domains;
            o.exams = this.exams;
            return o;
        }
    }
}

package com.certimaster.learning.domain.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.math.BigDecimal;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class CertificationInfo {

    private Long id;
    private String name;
    private String code;
    private String provider;
    private String level;
    private String description;
    private Integer durationMinutes;
    private Integer totalQuestions;
    private BigDecimal passingScore;
    private String status;
    private List<DomainInfo> domains;

    public CertificationInfo() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }

    public String getProvider() { return provider; }
    public void setProvider(String provider) { this.provider = provider; }

    public String getLevel() { return level; }
    public void setLevel(String level) { this.level = level; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Integer getDurationMinutes() { return durationMinutes; }
    public void setDurationMinutes(Integer durationMinutes) { this.durationMinutes = durationMinutes; }

    public Integer getTotalQuestions() { return totalQuestions; }
    public void setTotalQuestions(Integer totalQuestions) { this.totalQuestions = totalQuestions; }

    public BigDecimal getPassingScore() { return passingScore; }
    public void setPassingScore(BigDecimal passingScore) { this.passingScore = passingScore; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public List<DomainInfo> getDomains() { return domains; }
    public void setDomains(List<DomainInfo> domains) { this.domains = domains; }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private Long id;
        private String name;
        private String code;
        private String provider;
        private String level;
        private String description;
        private Integer durationMinutes;
        private Integer totalQuestions;
        private BigDecimal passingScore;
        private String status;
        private List<DomainInfo> domains;

        public Builder id(Long v) { this.id = v; return this; }
        public Builder name(String v) { this.name = v; return this; }
        public Builder code(String v) { this.code = v; return this; }
        public Builder provider(String v) { this.provider = v; return this; }
        public Builder level(String v) { this.level = v; return this; }
        public Builder description(String v) { this.description = v; return this; }
        public Builder durationMinutes(Integer v) { this.durationMinutes = v; return this; }
        public Builder totalQuestions(Integer v) { this.totalQuestions = v; return this; }
        public Builder passingScore(BigDecimal v) { this.passingScore = v; return this; }
        public Builder status(String v) { this.status = v; return this; }
        public Builder domains(List<DomainInfo> v) { this.domains = v; return this; }

        public CertificationInfo build() {
            CertificationInfo o = new CertificationInfo();
            o.id = this.id;
            o.name = this.name;
            o.code = this.code;
            o.provider = this.provider;
            o.level = this.level;
            o.description = this.description;
            o.durationMinutes = this.durationMinutes;
            o.totalQuestions = this.totalQuestions;
            o.passingScore = this.passingScore;
            o.status = this.status;
            o.domains = this.domains;
            return o;
        }
    }
}

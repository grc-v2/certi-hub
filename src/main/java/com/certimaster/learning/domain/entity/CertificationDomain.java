package com.certimaster.learning.domain.entity;

import com.certimaster.shared.entity.BaseEntity;
import com.certimaster.learning.domain.entity.Question;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "certification_domains")
public class CertificationDomain extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "certification_id", nullable = false)
    private Certification certification;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "code", length = 50)
    private String code;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "weight_percentage")
    private BigDecimal weightPercentage;

    @Column(name = "display_order")
    private Integer displayOrder;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_domain_id")
    private CertificationDomain parentDomain;

    @OneToMany(mappedBy = "parentDomain", fetch = FetchType.LAZY)
    private Set<CertificationDomain> childDomains = new HashSet<>();

    @Column(name = "status", length = 20)
    private String status;

    @OneToMany(mappedBy = "domain", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<Question> questions = new HashSet<>();

    public CertificationDomain() {
        super();
    }

    public Certification getCertification() { return certification; }
    public void setCertification(Certification certification) { this.certification = certification; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public BigDecimal getWeightPercentage() { return weightPercentage; }
    public void setWeightPercentage(BigDecimal weightPercentage) { this.weightPercentage = weightPercentage; }

    public Integer getDisplayOrder() { return displayOrder; }
    public void setDisplayOrder(Integer displayOrder) { this.displayOrder = displayOrder; }

    public CertificationDomain getParentDomain() { return parentDomain; }
    public void setParentDomain(CertificationDomain parentDomain) { this.parentDomain = parentDomain; }

    public Set<CertificationDomain> getChildDomains() { return childDomains; }
    public void setChildDomains(Set<CertificationDomain> childDomains) { this.childDomains = childDomains; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Set<Question> getQuestions() { return questions; }
    public void setQuestions(Set<Question> questions) { this.questions = questions; }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private Certification certification;
        private String name;
        private String code;
        private String description;
        private BigDecimal weightPercentage;
        private Integer displayOrder;
        private CertificationDomain parentDomain;
        private String status;
        private Set<Question> questions = new HashSet<>();

        public Builder certification(Certification v) { this.certification = v; return this; }
        public Builder name(String v) { this.name = v; return this; }
        public Builder code(String v) { this.code = v; return this; }
        public Builder description(String v) { this.description = v; return this; }
        public Builder weightPercentage(BigDecimal v) { this.weightPercentage = v; return this; }
        public Builder displayOrder(Integer v) { this.displayOrder = v; return this; }
        public Builder parentDomain(CertificationDomain v) { this.parentDomain = v; return this; }
        public Builder status(String v) { this.status = v; return this; }
        public Builder questions(Set<Question> v) { this.questions = v; return this; }

        public CertificationDomain build() {
            CertificationDomain o = new CertificationDomain();
            o.certification = this.certification;
            o.name = this.name;
            o.code = this.code;
            o.description = this.description;
            o.weightPercentage = this.weightPercentage;
            o.displayOrder = this.displayOrder;
            o.parentDomain = this.parentDomain;
            o.status = this.status;
            o.questions = this.questions;
            return o;
        }
    }
}

package com.certimaster.learning.adapter.web.response;

import com.certimaster.shared.dto.BaseResponse;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class CertificationDomainResponse extends BaseResponse {

    private Long id;
    private String name;
    private String code;
    private String description;
    private BigDecimal weightPercentage;
    private Integer displayOrder;
    private Long certificationId;
    private Long parentDomainId;
    private List<CertificationDomainResponse> children;
    private String status;

    public CertificationDomainResponse() {
    }

    private CertificationDomainResponse(Builder builder) {
        this.id = builder.id;
        this.name = builder.name;
        this.code = builder.code;
        this.description = builder.description;
        this.weightPercentage = builder.weightPercentage;
        this.displayOrder = builder.displayOrder;
        this.certificationId = builder.certificationId;
        this.parentDomainId = builder.parentDomainId;
        this.children = builder.children;
        this.status = builder.status;
        this.setCreatedAt(builder.createdAt);
        this.setUpdatedAt(builder.updatedAt);
        this.setCreatedBy(builder.createdBy);
        this.setUpdatedBy(builder.updatedBy);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BigDecimal getWeightPercentage() {
        return weightPercentage;
    }

    public void setWeightPercentage(BigDecimal weightPercentage) {
        this.weightPercentage = weightPercentage;
    }

    public Integer getDisplayOrder() {
        return displayOrder;
    }

    public void setDisplayOrder(Integer displayOrder) {
        this.displayOrder = displayOrder;
    }

    public Long getCertificationId() {
        return certificationId;
    }

    public void setCertificationId(Long certificationId) {
        this.certificationId = certificationId;
    }

    public Long getParentDomainId() {
        return parentDomainId;
    }

    public void setParentDomainId(Long parentDomainId) {
        this.parentDomainId = parentDomainId;
    }

    public List<CertificationDomainResponse> getChildren() {
        return children;
    }

    public void setChildren(List<CertificationDomainResponse> children) {
        this.children = children;
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
        private Long id;
        private String name;
        private String code;
        private String description;
        private BigDecimal weightPercentage;
        private Integer displayOrder;
        private Long certificationId;
        private Long parentDomainId;
        private List<CertificationDomainResponse> children;
        private String status;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
        private String createdBy;
        private String updatedBy;

        public Builder id(Long id) {
            this.id = id;
            return this;
        }

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder code(String code) {
            this.code = code;
            return this;
        }

        public Builder description(String description) {
            this.description = description;
            return this;
        }

        public Builder weightPercentage(BigDecimal weightPercentage) {
            this.weightPercentage = weightPercentage;
            return this;
        }

        public Builder displayOrder(Integer displayOrder) {
            this.displayOrder = displayOrder;
            return this;
        }

        public Builder certificationId(Long certificationId) {
            this.certificationId = certificationId;
            return this;
        }

        public Builder parentDomainId(Long parentDomainId) {
            this.parentDomainId = parentDomainId;
            return this;
        }

        public Builder children(List<CertificationDomainResponse> children) {
            this.children = children;
            return this;
        }

        public Builder status(String status) {
            this.status = status;
            return this;
        }

        public Builder createdAt(LocalDateTime createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public Builder updatedAt(LocalDateTime updatedAt) {
            this.updatedAt = updatedAt;
            return this;
        }

        public Builder createdBy(String createdBy) {
            this.createdBy = createdBy;
            return this;
        }

        public Builder updatedBy(String updatedBy) {
            this.updatedBy = updatedBy;
            return this;
        }

        public CertificationDomainResponse build() {
            return new CertificationDomainResponse(this);
        }
    }
}

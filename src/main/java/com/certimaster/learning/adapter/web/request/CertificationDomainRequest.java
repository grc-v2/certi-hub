package com.certimaster.learning.adapter.web.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class CertificationDomainRequest {

    @NotBlank(message = "Domain name is required")
    @Size(max = 255, message = "Domain name must not exceed 255 characters")
    private String name;

    @Size(max = 1000, message = "Description must not exceed 1000 characters")
    private String description;

    @NotNull(message = "Display order is required")
    private Integer displayOrder;

    private Long parentDomainId;

    public CertificationDomainRequest() {
    }

    private CertificationDomainRequest(Builder builder) {
        this.name = builder.name;
        this.description = builder.description;
        this.displayOrder = builder.displayOrder;
        this.parentDomainId = builder.parentDomainId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getDisplayOrder() {
        return displayOrder;
    }

    public void setDisplayOrder(Integer displayOrder) {
        this.displayOrder = displayOrder;
    }

    public Long getParentDomainId() {
        return parentDomainId;
    }

    public void setParentDomainId(Long parentDomainId) {
        this.parentDomainId = parentDomainId;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String name;
        private String description;
        private Integer displayOrder;
        private Long parentDomainId;

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder description(String description) {
            this.description = description;
            return this;
        }

        public Builder displayOrder(Integer displayOrder) {
            this.displayOrder = displayOrder;
            return this;
        }

        public Builder parentDomainId(Long parentDomainId) {
            this.parentDomainId = parentDomainId;
            return this;
        }

        public CertificationDomainRequest build() {
            return new CertificationDomainRequest(this);
        }
    }
}

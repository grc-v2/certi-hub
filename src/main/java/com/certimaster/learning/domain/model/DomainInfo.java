package com.certimaster.learning.domain.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.math.BigDecimal;

@JsonIgnoreProperties(ignoreUnknown = true)
public class DomainInfo {

    private Long id;
    private String name;
    private String code;
    private BigDecimal weightPercentage;
    private Integer displayOrder;

    public DomainInfo() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }

    public BigDecimal getWeightPercentage() { return weightPercentage; }
    public void setWeightPercentage(BigDecimal weightPercentage) { this.weightPercentage = weightPercentage; }

    public Integer getDisplayOrder() { return displayOrder; }
    public void setDisplayOrder(Integer displayOrder) { this.displayOrder = displayOrder; }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private Long id;
        private String name;
        private String code;
        private BigDecimal weightPercentage;
        private Integer displayOrder;

        public Builder id(Long v) { this.id = v; return this; }
        public Builder name(String v) { this.name = v; return this; }
        public Builder code(String v) { this.code = v; return this; }
        public Builder weightPercentage(BigDecimal v) { this.weightPercentage = v; return this; }
        public Builder displayOrder(Integer v) { this.displayOrder = v; return this; }

        public DomainInfo build() {
            DomainInfo o = new DomainInfo();
            o.id = this.id;
            o.name = this.name;
            o.code = this.code;
            o.weightPercentage = this.weightPercentage;
            o.displayOrder = this.displayOrder;
            return o;
        }
    }
}

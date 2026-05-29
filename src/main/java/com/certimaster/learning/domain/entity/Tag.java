package com.certimaster.learning.domain.entity;

import com.certimaster.shared.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

import java.time.LocalDateTime;

@Entity
@Table(name = "tags")
public class Tag extends BaseEntity {

    @Column(name = "name", nullable = false, unique = true, length = 100)
    private String name;

    @Column(name = "status", length = 20)
    private String status;

    public Tag() {
        super();
    }

    public Tag(String name, String status) {
        super();
        this.name = name;
        this.status = status;
    }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private String name;
        private String status;
        private LocalDateTime createdAt;
        private String createdBy;
        private LocalDateTime updatedAt;
        private String updatedBy;

        public Builder name(String v) { this.name = v; return this; }
        public Builder status(String v) { this.status = v; return this; }
        public Builder createdAt(LocalDateTime v) { this.createdAt = v; return this; }
        public Builder createdBy(String v) { this.createdBy = v; return this; }
        public Builder updatedAt(LocalDateTime v) { this.updatedAt = v; return this; }
        public Builder updatedBy(String v) { this.updatedBy = v; return this; }

        public Tag build() {
            Tag o = new Tag();
            o.name = this.name;
            o.status = this.status;
            if (this.createdAt != null) o.setCreatedAt(this.createdAt);
            if (this.createdBy != null) o.setCreatedBy(this.createdBy);
            if (this.updatedAt != null) o.setUpdatedAt(this.updatedAt);
            if (this.updatedBy != null) o.setUpdatedBy(this.updatedBy);
            return o;
        }
    }
}

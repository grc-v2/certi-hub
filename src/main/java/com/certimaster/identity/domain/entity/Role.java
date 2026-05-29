package com.certimaster.identity.domain.entity;

import com.certimaster.shared.entity.BaseEntity;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Table;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "roles")
public class Role extends BaseEntity {

    @Column(nullable = false, unique = true, length = 50)
    private String code;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(
            name = "role_permissions",
            joinColumns = @JoinColumn(name = "role_id")
    )
    @Column(name = "permission")
    private Set<String> permissions = new HashSet<>();

    public Role() {
        super();
    }

    public Role(Long id, java.time.LocalDateTime createdAt, java.time.LocalDateTime updatedAt, String createdBy, String updatedBy,
                String code, String name, String description, Set<String> permissions) {
        super();
        this.code = code;
        this.name = name;
        this.description = description;
        this.permissions = permissions;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
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

    public Set<String> getPermissions() {
        return permissions;
    }

    public void setPermissions(Set<String> permissions) {
        this.permissions = permissions;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String code;
        private String name;
        private String description;
        private Set<String> permissions = new HashSet<>();

        public Builder code(String v) { this.code = v; return this; }
        public Builder name(String v) { this.name = v; return this; }
        public Builder description(String v) { this.description = v; return this; }
        public Builder permissions(Set<String> v) { this.permissions = v; return this; }

        public Role build() {
            Role o = new Role();
            o.code = this.code;
            o.name = this.name;
            o.description = this.description;
            o.permissions = this.permissions;
            return o;
        }
    }
}

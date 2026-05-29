package com.certimaster.engagement.domain.entity;

import com.certimaster.engagement.domain.entity.TagCategory;
import com.certimaster.shared.entity.BaseEntity;
import com.certimaster.engagement.domain.entity.PostTagMapping;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "post_tags")
public class PostTag extends BaseEntity {

    @Column(unique = true, nullable = false, length = 100)
    private String name;

    @Column(unique = true, nullable = false, length = 100)
    private String slug;

    @Enumerated(EnumType.STRING)
    @Column(name = "tag_category", length = 30)
    private TagCategory tagCategory;

    @Column(columnDefinition = "TEXT")
    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_tag_id")
    private PostTag parentTag;

    @OneToMany(mappedBy = "parentTag", fetch = FetchType.LAZY)
    private Set<PostTag> children = new HashSet<>();

    @Column(name = "mapped_provider_id")
    private Long mappedProviderId;

    @Column(name = "mapped_certification_id")
    private Long mappedCertificationId;

    @Column(name = "mapped_domain_id")
    private Long mappedDomainId;

    @Column(name = "post_count")
    private Integer postCount = 0;

    @Column(name = "display_order")
    private Integer displayOrder = 0;

    @OneToMany(mappedBy = "tag", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private Set<PostTagMapping> postMappings = new HashSet<>();

    public PostTag() {
        super();
    }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getSlug() { return slug; }
    public void setSlug(String slug) { this.slug = slug; }

    public TagCategory getTagCategory() { return tagCategory; }
    public void setTagCategory(TagCategory tagCategory) { this.tagCategory = tagCategory; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public PostTag getParentTag() { return parentTag; }
    public void setParentTag(PostTag parentTag) { this.parentTag = parentTag; }

    public Set<PostTag> getChildren() { return children; }
    public void setChildren(Set<PostTag> children) { this.children = children; }

    public Long getMappedProviderId() { return mappedProviderId; }
    public void setMappedProviderId(Long mappedProviderId) { this.mappedProviderId = mappedProviderId; }

    public Long getMappedCertificationId() { return mappedCertificationId; }
    public void setMappedCertificationId(Long mappedCertificationId) { this.mappedCertificationId = mappedCertificationId; }

    public Long getMappedDomainId() { return mappedDomainId; }
    public void setMappedDomainId(Long mappedDomainId) { this.mappedDomainId = mappedDomainId; }

    public Integer getPostCount() { return postCount; }
    public void setPostCount(Integer postCount) { this.postCount = postCount; }

    public Integer getDisplayOrder() { return displayOrder; }
    public void setDisplayOrder(Integer displayOrder) { this.displayOrder = displayOrder; }

    public Set<PostTagMapping> getPostMappings() { return postMappings; }
    public void setPostMappings(Set<PostTagMapping> postMappings) { this.postMappings = postMappings; }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private String name;
        private String slug;
        private TagCategory tagCategory;
        private String description;

        public Builder name(String v) { this.name = v; return this; }
        public Builder slug(String v) { this.slug = v; return this; }
        public Builder tagCategory(TagCategory v) { this.tagCategory = v; return this; }
        public Builder description(String v) { this.description = v; return this; }

        public PostTag build() {
            PostTag o = new PostTag();
            o.name = this.name;
            o.slug = this.slug;
            o.tagCategory = this.tagCategory;
            o.description = this.description;
            return o;
        }
    }
}

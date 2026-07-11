package com.certimaster.engagement.domain.event;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public class PostPublishedEvent {

    private String eventId = UUID.randomUUID().toString();
    private Integer eventVersion = 1;
    private LocalDateTime occurredAt = LocalDateTime.now();
    private Long postId;
    private String title;
    private String slug;
    private String contentType;
    private String difficultyLevel;
    private Long authorId;
    private List<String> tagNames;
    private List<Long> certificationIds;
    private List<Long> domainIds;
    private LocalDateTime publishedAt;

    public PostPublishedEvent() {
    }

    public String getEventId() { return eventId; }
    public void setEventId(String eventId) { this.eventId = eventId; }

    public Integer getEventVersion() { return eventVersion; }
    public void setEventVersion(Integer eventVersion) { this.eventVersion = eventVersion; }

    public LocalDateTime getOccurredAt() { return occurredAt; }
    public void setOccurredAt(LocalDateTime occurredAt) { this.occurredAt = occurredAt; }

    public Long getPostId() { return postId; }
    public void setPostId(Long postId) { this.postId = postId; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getSlug() { return slug; }
    public void setSlug(String slug) { this.slug = slug; }

    public String getContentType() { return contentType; }
    public void setContentType(String contentType) { this.contentType = contentType; }

    public String getDifficultyLevel() { return difficultyLevel; }
    public void setDifficultyLevel(String difficultyLevel) { this.difficultyLevel = difficultyLevel; }

    public Long getAuthorId() { return authorId; }
    public void setAuthorId(Long authorId) { this.authorId = authorId; }

    public List<String> getTagNames() { return tagNames; }
    public void setTagNames(List<String> tagNames) { this.tagNames = tagNames; }

    public List<Long> getCertificationIds() { return certificationIds; }
    public void setCertificationIds(List<Long> certificationIds) { this.certificationIds = certificationIds; }

    public List<Long> getDomainIds() { return domainIds; }
    public void setDomainIds(List<Long> domainIds) { this.domainIds = domainIds; }

    public LocalDateTime getPublishedAt() { return publishedAt; }
    public void setPublishedAt(LocalDateTime publishedAt) { this.publishedAt = publishedAt; }
}

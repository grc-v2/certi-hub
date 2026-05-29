package com.certimaster.engagement.adapter.persistence.projection;

import java.time.LocalDateTime;

public interface PostSummaryProjection {

    Long getId();

    LocalDateTime getCreatedAt();

    LocalDateTime getUpdatedAt();

    String getCreatedBy();

    String getUpdatedBy();

    String getTitle();

    String getSlug();

    String getExcerpt();

    String getFeaturedImage();

    Long getAuthorId();

    String getStatus();

    LocalDateTime getPublishedAt();

    Integer getViewsCount();

    Integer getLikesCount();

    Integer getCommentsCount();

    Integer getReadingTimeMinutes();

    String getContentType();

    String getDifficultyLevel();

    String getSubtitle();

    String getCoverImageUrl();

    Boolean getPinned();

    Boolean getFeatured();

    String getCategory();
}

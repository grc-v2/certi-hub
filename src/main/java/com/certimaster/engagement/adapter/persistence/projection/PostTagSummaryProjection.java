package com.certimaster.engagement.adapter.persistence.projection;

public interface PostTagSummaryProjection {

    Long getPostId();

    Long getTagId();

    String getName();

    String getSlug();
}

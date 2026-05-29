package com.certimaster.engagement.application.query;

import java.time.LocalDateTime;

public record SearchPostsQuery(
        String keyword,
        String status,
        Long authorId,
        Long categoryId,
        Long tagId,
        String contentType,
        String difficultyLevel,
        Long seriesId,
        String fullTextQuery,
        LocalDateTime publishedFrom,
        LocalDateTime publishedTo,
        Integer page,
        Integer size,
        String sortBy,
        String sortDirection
) {
}

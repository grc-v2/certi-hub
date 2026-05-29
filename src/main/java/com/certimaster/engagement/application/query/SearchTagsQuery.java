package com.certimaster.engagement.application.query;

public record SearchTagsQuery(
        String keyword,
        Integer page,
        Integer size,
        String sortBy,
        String sortDirection
) {
}

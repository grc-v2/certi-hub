package com.certimaster.learning.application.query;

public record SearchTagsQuery(
        String keyword,
        String status,
        Integer page,
        Integer size,
        String sortBy,
        String sortDirection
) {
}

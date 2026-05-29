package com.certimaster.learning.application.query;

public record SearchQuestionsQuery(
        String keyword,
        Long domainId,
        Long certificationId,
        String type,
        String difficulty,
        String status,
        Integer page,
        Integer size,
        String sortBy,
        String sortDirection
) {
}

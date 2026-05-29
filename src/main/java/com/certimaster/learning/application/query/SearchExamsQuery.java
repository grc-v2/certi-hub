package com.certimaster.learning.application.query;

public record SearchExamsQuery(
        String keyword,
        Long certificationId,
        String type,
        String status,
        Integer page,
        Integer size,
        String sortBy,
        String sortDirection
) {
}

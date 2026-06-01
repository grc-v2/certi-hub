package com.certimaster.learning.application.query;

public record SearchCoursesQuery(
        Long certificationId,
        String search,
        int page,
        int size,
        String sortBy,
        String sortDirection
) {
}

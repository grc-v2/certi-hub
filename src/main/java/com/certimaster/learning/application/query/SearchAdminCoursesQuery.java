package com.certimaster.learning.application.query;

public record SearchAdminCoursesQuery(
        Long certificationId,
        String search,
        Boolean published,
        int page,
        int size,
        String sortBy,
        String sortDirection
) {
}

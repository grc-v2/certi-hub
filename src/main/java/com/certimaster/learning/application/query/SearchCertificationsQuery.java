package com.certimaster.learning.application.query;

public record SearchCertificationsQuery(
        String keyword,
        String provider,
        String level,
        String status,
        Long providerId,
        String difficultyLevel,
        Integer page,
        Integer size,
        String sortBy,
        String sortDirection
) {
}

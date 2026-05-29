package com.certimaster.learning.application.query;

public record SearchCertificationDomainsQuery(
        String keyword,
        Integer page,
        Integer size,
        String sortBy,
        String sortDir
) {
}

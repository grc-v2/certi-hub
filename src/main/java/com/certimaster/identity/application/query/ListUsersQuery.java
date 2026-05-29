package com.certimaster.identity.application.query;

import org.springframework.data.domain.Pageable;

import java.time.LocalDate;

public record ListUsersQuery(
        Pageable pageable,
        String search,
        String roleCode,
        String status,
        LocalDate registrationDateFrom,
        LocalDate registrationDateTo
) {
}

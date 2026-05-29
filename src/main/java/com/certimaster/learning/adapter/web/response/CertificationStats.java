package com.certimaster.learning.adapter.web.response;

import java.math.BigDecimal;

/**
 * Statistics for a specific certification in the user's exam history.
 * Contains aggregated metrics for all exams taken under this certification.
 */
public record CertificationStats(
    Long certificationId,
    String name,
    Integer exams,
    BigDecimal averageScore
) {}

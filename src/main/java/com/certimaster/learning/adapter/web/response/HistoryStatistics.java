package com.certimaster.learning.adapter.web.response;

import java.math.BigDecimal;
import java.util.List;

/**
 * Aggregated statistics for a user's exam history.
 * Contains overall metrics and breakdown by certification.
 */
public record HistoryStatistics(
    Integer totalExams,
    Integer totalPassed,
    Integer totalFailed,
    BigDecimal passRate,
    BigDecimal averageScore,
    BigDecimal bestScore,
    Integer totalTimeSpent,
    Integer averageTimePerExam,
    List<CertificationStats> certifications
) {}

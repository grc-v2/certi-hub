package com.certimaster.learning.adapter.web.response;

import java.math.BigDecimal;

/**
 * Summary statistics for an exam result.
 * Contains overall performance metrics including scores, timing, and pass/fail status.
 */
public record ResultSummary(
    Integer totalQuestions,
    Integer answeredQuestions,
    Integer unansweredQuestions,
    Integer correctAnswers,
    Integer wrongAnswers,
    Integer score,
    BigDecimal percentage,
    Integer passingScore,
    String status,
    Integer totalTimeSpent,
    Integer averageTimePerQuestion
) {}

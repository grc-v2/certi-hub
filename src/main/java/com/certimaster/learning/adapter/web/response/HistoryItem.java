package com.certimaster.learning.adapter.web.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Represents a single exam result item in the user's exam history.
 * Contains summary information about a completed exam attempt.
 */
public record HistoryItem(
    Long resultId,
    Long examId,
    String examTitle,
    String examType,
    Long certificationId,
    String certificationName,
    Integer score,
    BigDecimal percentage,
    String status,
    Integer totalQuestions,
    Integer correctAnswers,
    Integer timeSpent,
    LocalDateTime completedAt,
    Integer attemptNumber
) {}

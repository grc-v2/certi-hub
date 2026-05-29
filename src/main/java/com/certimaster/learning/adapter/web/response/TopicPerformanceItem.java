package com.certimaster.learning.adapter.web.response;

import java.math.BigDecimal;
import java.util.List;

/**
 * Performance metrics for a specific topic within an exam result.
 * Contains question counts, accuracy percentage, status assessment, and recommendations.
 */
public record TopicPerformanceItem(
    Long topicId,
    String topicName,
    BigDecimal weight,
    Integer totalQuestions,
    Integer correctAnswers,
    Integer wrongAnswers,
    BigDecimal percentage,
    String status,
    List<String> recommendations
) {}

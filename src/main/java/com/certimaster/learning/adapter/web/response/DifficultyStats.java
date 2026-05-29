package com.certimaster.learning.adapter.web.response;

import java.math.BigDecimal;

/**
 * Statistics for a specific difficulty level.
 * Contains total questions, correct answers, and accuracy percentage.
 */
public record DifficultyStats(
    Integer total,
    Integer correct,
    BigDecimal percentage
) {}

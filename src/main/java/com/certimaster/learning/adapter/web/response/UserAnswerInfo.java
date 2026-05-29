package com.certimaster.learning.adapter.web.response;

import java.util.List;

/**
 * Information about the user's answer to a question.
 * Contains the selected options, correctness status, and time spent.
 */
public record UserAnswerInfo(
    List<Long> selectedOptionIds,
    List<String> selectedOptions,
    Boolean isCorrect,
    Integer timeSpent
) {}

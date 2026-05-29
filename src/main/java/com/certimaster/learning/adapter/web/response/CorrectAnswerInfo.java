package com.certimaster.learning.adapter.web.response;

import java.util.List;

/**
 * Information about the correct answer for a question.
 * Contains the correct option IDs and their text content.
 */
public record CorrectAnswerInfo(
    List<Long> correctOptionIds,
    List<String> correctOptions
) {}

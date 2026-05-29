package com.certimaster.learning.adapter.web.response;

/**
 * Summary counts for questions in a question report.
 * Contains totals for all questions, correct, wrong, flagged, and reviewed.
 */
public record QuestionSummary(
    Integer totalQuestions,
    Integer correct,
    Integer wrong,
    Integer flagged,
    Integer reviewed
) {}

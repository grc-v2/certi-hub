package com.certimaster.learning.adapter.web.response;

import java.util.List;

/**
 * Detailed report item for a single question in an exam result.
 * Contains question details, user's answer, correct answer, explanation, and reference links.
 */
public record QuestionReportItem(
    Long questionId,
    Integer questionNumber,
    Long topicId,
    String topicName,
    String difficulty,
    String type,
    String content,
    UserAnswerInfo userAnswer,
    CorrectAnswerInfo correctAnswer,
    String explanation,
    List<ReferenceLink> referenceLinks,
    Boolean wasFlagged,
    Boolean wasReviewed
) {}

package com.certimaster.learning.application.query;

public record GetQuestionReportQuery(
        Long resultId,
        Long currentUserId,
        Integer page,
        Integer size,
        String filter,
        Long topicId,
        String difficulty
) {
}

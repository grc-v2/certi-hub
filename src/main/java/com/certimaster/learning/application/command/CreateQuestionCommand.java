package com.certimaster.learning.application.command;

import java.util.List;

public record CreateQuestionCommand(
        Long domainId,
        String type,
        String content,
        String explanation,
        String difficulty,
        Integer points,
        Integer timeLimitSeconds,
        String referenceUrl,
        List<QuestionOptionCommand> options
) {
}

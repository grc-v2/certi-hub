package com.certimaster.learning.application.command;

import java.util.List;

public record BulkQuestionCommand(
        String questionText,
        String difficulty,
        String questionType,
        String explanation,
        List<QuestionOptionCommand> options
) {
}

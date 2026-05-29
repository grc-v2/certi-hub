package com.certimaster.learning.application.command;

import java.util.List;

public record BulkImportQuestionsCommand(
        Long certificationId,
        Long domainId,
        List<BulkQuestionCommand> questions
) {
}

package com.certimaster.learning.application.command;

import java.util.List;

public record SubmitAnswerCommand(
        Long sessionId,
        Long userId,
        Long questionId,
        List<Long> selectedOptionIds,
        Integer timeSpentSeconds,
        Boolean isFlagged
) {
}

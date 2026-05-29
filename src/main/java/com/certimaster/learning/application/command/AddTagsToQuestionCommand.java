package com.certimaster.learning.application.command;

import java.util.List;

public record AddTagsToQuestionCommand(
        Long questionId,
        List<Long> tagIds
) {
}

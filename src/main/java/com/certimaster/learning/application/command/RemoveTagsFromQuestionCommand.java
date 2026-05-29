package com.certimaster.learning.application.command;

import java.util.List;

public record RemoveTagsFromQuestionCommand(
        Long questionId,
        List<Long> tagIds
) {
}

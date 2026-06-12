package com.certimaster.learning.application.query;

import java.util.List;

public record GetFlashcardQuestionsQuery(
        Long certificationId,
        List<Long> domainIds,
        List<String> difficulties,
        Integer count
) {
}

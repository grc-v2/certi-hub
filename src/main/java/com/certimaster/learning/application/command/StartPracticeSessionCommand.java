package com.certimaster.learning.application.command;

import java.util.List;

public record StartPracticeSessionCommand(
        Long userId,
        Long certificationId,
        Long questionBankId,
        List<Long> domainIds,
        List<String> difficulties,
        Integer questionCount
) {
}

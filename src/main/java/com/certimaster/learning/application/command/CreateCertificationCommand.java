package com.certimaster.learning.application.command;

import java.math.BigDecimal;

public record CreateCertificationCommand(
        String name,
        String code,
        String provider,
        String description,
        String level,
        Integer durationMinutes,
        BigDecimal passingScore,
        Integer totalQuestions,
        BigDecimal price,
        String status
) {
}

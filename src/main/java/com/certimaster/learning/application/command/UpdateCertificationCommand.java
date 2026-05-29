package com.certimaster.learning.application.command;

import java.math.BigDecimal;

public record UpdateCertificationCommand(
        Long id,
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

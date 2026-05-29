package com.certimaster.learning.application.command;

public record UpdateExamCommand(
        Long id,
        Long certificationId,
        String title,
        String type,
        String description,
        Integer durationMinutes,
        Integer totalQuestions,
        Integer passingScore,
        String status
) {
}

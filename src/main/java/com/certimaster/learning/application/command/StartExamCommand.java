package com.certimaster.learning.application.command;

public record StartExamCommand(
        Long examId,
        Long userId,
        String username,
        String mode
) {
}

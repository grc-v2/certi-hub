package com.certimaster.learning.application.command;

public record PauseSessionCommand(
        Long sessionId,
        Long userId,
        Integer timeSpentSeconds
) {
}

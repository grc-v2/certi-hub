package com.certimaster.learning.application.command;

public record CompleteSessionCommand(
        Long sessionId,
        Long userId
) {
}

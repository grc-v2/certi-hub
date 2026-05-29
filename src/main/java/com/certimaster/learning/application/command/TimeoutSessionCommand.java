package com.certimaster.learning.application.command;

public record TimeoutSessionCommand(
        Long sessionId,
        Long userId
) {
}

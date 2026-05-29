package com.certimaster.learning.application.command;

public record AbandonSessionCommand(
        Long sessionId,
        Long userId
) {
}

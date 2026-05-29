package com.certimaster.identity.application.command;

public record ForceLogoutUserSessionCommand(
        Long userId,
        String sessionId
) {
}

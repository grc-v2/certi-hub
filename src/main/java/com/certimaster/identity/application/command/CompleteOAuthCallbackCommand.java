package com.certimaster.identity.application.command;

public record CompleteOAuthCallbackCommand(
        String provider,
        String code,
        String state
) {
}

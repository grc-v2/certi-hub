package com.certimaster.identity.application.command;

public record StartOAuthAuthorizationCommand(
        String provider,
        String redirectUri
) {
}

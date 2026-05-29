package com.certimaster.identity.application.command;

public record LoginUserCommand(
        String username,
        String password
) {
}

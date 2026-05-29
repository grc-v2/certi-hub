package com.certimaster.identity.application.command;

public record RegisterUserCommand(
        String email,
        String username,
        String password,
        String fullName,
        String phone
) {
}

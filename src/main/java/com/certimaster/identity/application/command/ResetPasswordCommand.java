package com.certimaster.identity.application.command;

public record ResetPasswordCommand(
        String token,
        String newPassword
) {
}

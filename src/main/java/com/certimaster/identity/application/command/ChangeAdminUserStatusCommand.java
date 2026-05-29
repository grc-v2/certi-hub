package com.certimaster.identity.application.command;

public record ChangeAdminUserStatusCommand(
        Long id,
        String status
) {
}

package com.certimaster.identity.application.command;

public record UpdateAdminUserCommand(
        Long id,
        String email,
        String fullName,
        String phone,
        String roleCode,
        String status
) {
}

package com.certimaster.learning.application.command;

public record UpdateTagCommand(
        Long id,
        String name,
        String status
) {
}

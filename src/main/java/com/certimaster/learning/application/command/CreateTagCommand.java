package com.certimaster.learning.application.command;

public record CreateTagCommand(
        String name,
        String status
) {
}

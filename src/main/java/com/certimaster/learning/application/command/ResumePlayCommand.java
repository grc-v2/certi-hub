package com.certimaster.learning.application.command;

public record ResumePlayCommand(
        Long sessionId,
        Long userId
) {
}

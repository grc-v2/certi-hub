package com.certimaster.engagement.application.command;

import java.time.LocalDateTime;

public record SchedulePostCommand(Long id, LocalDateTime scheduledAt, Long userId) {
}

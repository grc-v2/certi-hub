package com.certimaster.engagement.application.command;

public record CreateNotificationCommand(Long userId, String type, String title,
                                        String message, String relatedEntityId) {
}

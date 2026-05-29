package com.certimaster.engagement.application.command;

public record ToggleBookmarkCommand(Long postId, Long userId, String notes) {
}

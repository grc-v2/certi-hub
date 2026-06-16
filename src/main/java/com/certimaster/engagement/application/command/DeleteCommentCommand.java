package com.certimaster.engagement.application.command;

public record DeleteCommentCommand(Long commentId, Long postId, Long userId, boolean admin) {
}

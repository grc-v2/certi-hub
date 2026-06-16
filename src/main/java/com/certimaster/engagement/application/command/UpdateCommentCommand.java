package com.certimaster.engagement.application.command;

public record UpdateCommentCommand(Long commentId, Long postId, Long userId,
                                   boolean admin, String content) {
}

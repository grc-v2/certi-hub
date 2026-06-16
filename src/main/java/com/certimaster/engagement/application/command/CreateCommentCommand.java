package com.certimaster.engagement.application.command;

public record CreateCommentCommand(Long postId, Long parentId, Long authorId,
                                   String authorName, String content) {
}

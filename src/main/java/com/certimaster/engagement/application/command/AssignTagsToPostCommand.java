package com.certimaster.engagement.application.command;

import java.util.List;

public record AssignTagsToPostCommand(Long postId, List<String> tagNames) {
}

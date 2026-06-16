package com.certimaster.productivity.application.command;

import com.certimaster.productivity.domain.entity.ResourceKind;

/**
 * Create a resource. When {@code url} is blank it becomes a NOTE; otherwise the
 * kind is detected from the URL unless {@code kindOverride} is supplied, and the
 * title is auto-fetched when {@code title} is blank.
 */
public record CreateResourceCommand(
        Long userId,
        String url,
        String title,
        String content,
        ResourceKind kindOverride,
        Long collectionId
) {
}

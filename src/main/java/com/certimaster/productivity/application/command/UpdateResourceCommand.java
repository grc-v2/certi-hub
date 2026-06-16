package com.certimaster.productivity.application.command;

/**
 * Update a resource's editable fields. {@code title} and {@code content} are
 * always applied; for a link resource, changing {@code url} re-detects the kind.
 * Null {@code url} leaves the existing URL untouched.
 */
public record UpdateResourceCommand(
        Long id,
        Long userId,
        String title,
        String content,
        String url
) {
}

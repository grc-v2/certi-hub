package com.certimaster.productivity.application.command;

/** Assign a resource to a collection, or detach it when {@code collectionId} is null. */
public record SetResourceCollectionCommand(Long resourceId, Long userId, Long collectionId) {
}

package com.certimaster.productivity.application.query;

import com.certimaster.productivity.domain.entity.ResourceKind;

/** Optional {@code kind} and/or {@code collectionId} narrow the result. */
public record ListResourcesQuery(Long userId, ResourceKind kind, Long collectionId) {
}

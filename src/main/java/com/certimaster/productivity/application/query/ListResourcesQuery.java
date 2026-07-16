package com.certimaster.productivity.application.query;

import com.certimaster.productivity.domain.entity.ResourceKind;

/** Optional {@code kind}, {@code collectionId} and/or {@code certificationId} narrow the result. */
public record ListResourcesQuery(Long userId, ResourceKind kind, Long collectionId, Long certificationId) {
}

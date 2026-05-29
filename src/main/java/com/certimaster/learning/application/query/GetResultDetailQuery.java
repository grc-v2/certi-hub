package com.certimaster.learning.application.query;

public record GetResultDetailQuery(
        Long resultId,
        Long currentUserId
) {
}

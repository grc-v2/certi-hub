package com.certimaster.engagement.application.query;

public record GetPostsByAuthorQuery(Long authorId, int page, int size) {
}

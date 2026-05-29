package com.certimaster.engagement.application.query;

public record GetUserBookmarksQuery(Long userId, int page, int size) {
}

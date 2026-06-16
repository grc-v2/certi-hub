package com.certimaster.engagement.application.query;

public record ListNotificationsQuery(Long userId, boolean unreadOnly, int page, int size) {
}

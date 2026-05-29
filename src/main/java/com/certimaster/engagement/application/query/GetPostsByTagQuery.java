package com.certimaster.engagement.application.query;

public record GetPostsByTagQuery(String tagSlug, int page, int size) {
}

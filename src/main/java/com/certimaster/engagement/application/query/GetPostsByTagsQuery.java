package com.certimaster.engagement.application.query;

import java.util.List;

public record GetPostsByTagsQuery(List<String> tagNames, int page, int size) {
}

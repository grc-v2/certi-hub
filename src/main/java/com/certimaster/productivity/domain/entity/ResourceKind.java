package com.certimaster.productivity.domain.entity;

/**
 * Classifies a {@link TrackerResource} by its source. Link resources are detected
 * from their URL host; {@code NOTE} is a pure text resource with no URL;
 * {@code PDF} is an uploaded ebook/file stored in object storage (the {@code url}
 * column holds the storage object key, not a public link).
 */
public enum ResourceKind {
    YOUTUBE,
    GITHUB,
    BLOG,
    NOTE,
    PDF
}

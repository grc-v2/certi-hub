package com.certimaster.productivity.domain.service;

import com.certimaster.productivity.domain.entity.ResourceKind;

import java.net.URI;

/**
 * Pure host-based classification of a resource URL into a {@link ResourceKind}.
 * No URL (blank) means a text note; YouTube/GitHub hosts map to their kind; any
 * other link falls back to {@code BLOG} (the catch-all web resource).
 */
public final class ResourceKindDetector {

    private ResourceKindDetector() {
    }

    public static ResourceKind detect(String url) {
        String host = hostOf(url);
        if (host == null) {
            return ResourceKind.NOTE;
        }
        if (host.equals("youtube.com") || host.endsWith(".youtube.com") || host.equals("youtu.be")) {
            return ResourceKind.YOUTUBE;
        }
        if (host.equals("github.com") || host.endsWith(".github.com")) {
            return ResourceKind.GITHUB;
        }
        return ResourceKind.BLOG;
    }

    /** Lower-cased host without a leading {@code www.}, or {@code null} if the URL is blank/unparseable. */
    public static String hostOf(String url) {
        if (url == null || url.isBlank()) {
            return null;
        }
        String normalized = url.trim();
        if (!normalized.matches("(?i)^[a-z][a-z0-9+.-]*://.*")) {
            normalized = "https://" + normalized;
        }
        try {
            String host = URI.create(normalized).getHost();
            if (host == null || host.isBlank()) {
                return null;
            }
            host = host.toLowerCase();
            return host.startsWith("www.") ? host.substring(4) : host;
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}

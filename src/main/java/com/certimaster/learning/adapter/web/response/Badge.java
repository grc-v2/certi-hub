package com.certimaster.learning.adapter.web.response;

/**
 * Achievement badge earned by a user.
 * Contains badge type, display name, description, and icon reference.
 */
public record Badge(
    String type,
    String name,
    String description,
    String icon
) {}

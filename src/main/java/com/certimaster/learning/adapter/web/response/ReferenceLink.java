package com.certimaster.learning.adapter.web.response;

/**
 * A reference link for additional learning resources related to a question.
 * Contains the title and URL of the reference material.
 */
public record ReferenceLink(
    String title,
    String url
) {}

package com.certimaster.engagement.application.command;

import java.util.List;

public record CreatePostCommand(
        String title,
        String content,
        String excerpt,
        String featuredImage,
        List<Long> categoryIds,
        List<String> tagNames,
        String seoTitle,
        String seoDescription,
        String seoKeywords,
        String contentType,
        String difficultyLevel,
        String subtitle,
        String coverImageUrl,
        String coverImageAlt,
        String ogImageUrl,
        Long seriesId,
        Long authorId
) {
}

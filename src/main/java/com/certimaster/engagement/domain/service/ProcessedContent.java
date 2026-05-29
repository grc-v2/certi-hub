package com.certimaster.engagement.domain.service;

public record ProcessedContent(String contentHtml,
                               String tocJson,
                               int wordCount,
                               int readingTimeMinutes,
                               String excerpt) {
}

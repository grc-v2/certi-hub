package com.certimaster.engagement.domain.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class PostContentProcessor {

    private static final Logger log = LoggerFactory.getLogger(PostContentProcessor.class);
    private static final Pattern HEADING_PATTERN = Pattern.compile("<h([2-4])[^>]*id=\"([^\"]*)\"[^>]*>(.*?)</h[2-4]>", Pattern.CASE_INSENSITIVE);
    private static final Pattern HEADING_MARKDOWN_PATTERN = Pattern.compile("^(#{2,4})\\s+(.+)$", Pattern.MULTILINE);
    private static final int EXCERPT_MAX_LENGTH = 300;

    private final MarkdownService markdownService;
    private final ReadingTimeCalculator readingTimeCalculator;
    private final ObjectMapper objectMapper;

    public PostContentProcessor(MarkdownService markdownService,
                                ReadingTimeCalculator readingTimeCalculator,
                                ObjectMapper objectMapper) {
        this.markdownService = markdownService;
        this.readingTimeCalculator = readingTimeCalculator;
        this.objectMapper = objectMapper;
    }

    public ProcessedContent process(String markdownContent) {
        if (markdownContent == null || markdownContent.isBlank()) {
            return new ProcessedContent("", "[]", 0, 0, "");
        }

        String contentHtml = markdownService.renderToHtml(markdownContent);
        String tocJson = generateTableOfContents(markdownContent);
        int wordCount = readingTimeCalculator.countWords(markdownContent);
        int readingTimeMinutes = readingTimeCalculator.calculateReadingTime(markdownContent);
        String excerpt = generateExcerpt(markdownContent);

        return new ProcessedContent(contentHtml, tocJson, wordCount, readingTimeMinutes, excerpt);
    }

    private String generateTableOfContents(String markdown) {
        List<TocEntry> entries = new ArrayList<>();
        Matcher matcher = HEADING_MARKDOWN_PATTERN.matcher(markdown);

        while (matcher.find()) {
            int level = matcher.group(1).length();
            String text = matcher.group(2).trim();
            String id = text.toLowerCase()
                    .replaceAll("[^a-z0-9\\s-]", "")
                    .replaceAll("\\s+", "-");
            entries.add(new TocEntry(id, text, level));
        }

        try {
            return objectMapper.writeValueAsString(entries);
        } catch (JacksonException e) {
            log.warn("Failed to serialize ToC: {}", e.getMessage());
            return "[]";
        }
    }

    private String generateExcerpt(String markdown) {
        String plain = markdown
                .replaceAll("(?m)^#{1,6}\\s+.*$", "")
                .replaceAll("\\[([^]]+)]\\([^)]+\\)", "$1")
                .replaceAll("[*_`~#>|\\-]", "")
                .replaceAll("(?m)^\\s*$", "")
                .trim();

        if (plain.length() <= EXCERPT_MAX_LENGTH) {
            return plain;
        }
        int cutoff = plain.lastIndexOf(' ', EXCERPT_MAX_LENGTH);
        if (cutoff < 0) cutoff = EXCERPT_MAX_LENGTH;
        return plain.substring(0, cutoff) + "...";
    }
}

package com.certimaster.engagement.domain.service;

import org.springframework.stereotype.Component;

@Component
public class ReadingTimeCalculator {

    private static final int WORDS_PER_MINUTE = 200;

    public int calculateReadingTime(String content) {
        if (content == null || content.isBlank()) {
            return 0;
        }

        int wordCount = countWords(content);
        
        if (wordCount == 0) {
            return 0;
        }

        // Calculate reading time: ceil(wordCount / 200)
        return (int) Math.ceil((double) wordCount / WORDS_PER_MINUTE);
    }

    public int countWords(String content) {
        if (content == null || content.isBlank()) {
            return 0;
        }

        // Split by whitespace and count non-empty tokens
        String[] words = content.trim().split("\\s+");
        
        int count = 0;
        for (String word : words) {
            if (!word.isEmpty()) {
                count++;
            }
        }
        
        return count;
    }
}

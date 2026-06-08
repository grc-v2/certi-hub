package com.certimaster.engagement.domain.service;

import org.junit.jupiter.api.Test;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.json.JsonMapper;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class PostContentProcessorTest {

    private final MarkdownService markdownService = mock(MarkdownService.class);
    private final ReadingTimeCalculator readingTimeCalculator = new ReadingTimeCalculator();
    private final ObjectMapper objectMapper = new JsonMapper();
    private final PostContentProcessor processor =
            new PostContentProcessor(markdownService, readingTimeCalculator, objectMapper);

    @Test
    void processSerializesTableOfContentsWithJackson3Mapper() {
        String markdown = """
                ## First Heading
                body
                ### Nested Topic
                more body
                #### Deep Dive
                """;
        when(markdownService.renderToHtml(markdown)).thenReturn("<h2 id=\"first-heading\">First Heading</h2>");

        ProcessedContent result = processor.process(markdown);

        @SuppressWarnings("unchecked")
        List<Map<String, Object>> toc = objectMapper.readValue(result.tocJson(), List.class);
        assertThat(toc).hasSize(3);
        assertThat(toc.getFirst())
                .containsEntry("id", "first-heading")
                .containsEntry("text", "First Heading")
                .containsEntry("level", 2);
        assertThat(toc.get(1))
                .containsEntry("id", "nested-topic")
                .containsEntry("level", 3);
        assertThat(result.contentHtml()).isEqualTo("<h2 id=\"first-heading\">First Heading</h2>");
        assertThat(result.wordCount()).isGreaterThan(0);
        assertThat(result.readingTimeMinutes()).isGreaterThan(0);
    }
}

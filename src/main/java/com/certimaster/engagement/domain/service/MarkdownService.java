package com.certimaster.engagement.domain.service;

import com.vladsch.flexmark.html.HtmlRenderer;
import com.vladsch.flexmark.parser.Parser;
import com.vladsch.flexmark.util.ast.Node;
import org.owasp.html.PolicyFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class MarkdownService {

    private static final Logger log = LoggerFactory.getLogger(MarkdownService.class);

    private final Parser markdownParser;
    private final HtmlRenderer markdownRenderer;
    private final PolicyFactory htmlSanitizer;

    public MarkdownService(Parser markdownParser, HtmlRenderer markdownRenderer, PolicyFactory htmlSanitizer) {
        this.markdownParser = markdownParser;
        this.markdownRenderer = markdownRenderer;
        this.htmlSanitizer = htmlSanitizer;
    }

    public String renderToHtml(String markdown) {
        if (markdown == null || markdown.isEmpty()) {
            return "";
        }

        try {
            // Parse Markdown to AST
            Node document = markdownParser.parse(markdown);

            // Render AST to HTML
            String html = markdownRenderer.render(document);

            // Sanitize HTML to prevent XSS
            return sanitizeHtml(html);
        } catch (Exception e) {
            // Requirement 11.4: Handle invalid Markdown gracefully
            log.warn("Failed to render Markdown, returning escaped content: {}", e.getMessage());
            return escapeHtml(markdown);
        }
    }

    public String sanitizeHtml(String html) {
        if (html == null || html.isEmpty()) {
            return "";
        }

        // Requirement 11.5: Sanitize HTML to prevent XSS attacks
        return htmlSanitizer.sanitize(html);
    }

    /**
     * Escape HTML special characters for safe display.
     * Used as fallback when Markdown parsing fails.
     */
    private String escapeHtml(String text) {
        if (text == null) {
            return "";
        }
        return text
                .replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&#x27;");
    }
}

package com.certimaster.shared.config;

import com.vladsch.flexmark.ext.autolink.AutolinkExtension;
import com.vladsch.flexmark.ext.gfm.strikethrough.StrikethroughExtension;
import com.vladsch.flexmark.ext.gfm.tasklist.TaskListExtension;
import com.vladsch.flexmark.ext.tables.TablesExtension;
import com.vladsch.flexmark.html.HtmlRenderer;
import com.vladsch.flexmark.parser.Parser;
import com.vladsch.flexmark.util.data.MutableDataSet;
import org.owasp.html.HtmlPolicyBuilder;
import org.owasp.html.PolicyFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;

/**
 * Configuration for Markdown processing with flexmark-java
 * 
 * Requirements:
 * - 11.2: Render Markdown to HTML using server-side renderer
 * - 11.3: Apply syntax highlighting for code blocks with language specification
 */
@Configuration
public class MarkdownConfig {

    @Value("${markdown.syntax-highlighting:true}")
    private boolean syntaxHighlighting;

    @Value("${markdown.gfm-extensions:true}")
    private boolean gfmExtensions;

    /**
     * Configure flexmark-java parser with GitHub Flavored Markdown extensions
     */
    @Bean
    public Parser markdownParser() {
        MutableDataSet options = new MutableDataSet();

        if (gfmExtensions) {
            options.set(Parser.EXTENSIONS, Arrays.asList(
                    TablesExtension.create(),
                    StrikethroughExtension.create(),
                    AutolinkExtension.create(),
                    TaskListExtension.create()
            ));
        }

        return Parser.builder(options).build();
    }

    /**
     * Configure flexmark-java HTML renderer with syntax highlighting support
     */
    @Bean
    public HtmlRenderer markdownRenderer() {
        MutableDataSet options = new MutableDataSet();

        if (gfmExtensions) {
            options.set(Parser.EXTENSIONS, Arrays.asList(
                    TablesExtension.create(),
                    StrikethroughExtension.create(),
                    AutolinkExtension.create(),
                    TaskListExtension.create()
            ));
        }

        // Configure code block rendering with language class for syntax highlighting
        options.set(HtmlRenderer.FENCED_CODE_LANGUAGE_CLASS_PREFIX, "language-");
        options.set(HtmlRenderer.FENCED_CODE_NO_LANGUAGE_CLASS, "language-text");

        return HtmlRenderer.builder(options).build();
    }

    /**
     * Configure OWASP HTML Sanitizer policy for XSS protection
     * Allows safe HTML elements while blocking potentially dangerous content
     */
    @Bean
    public PolicyFactory htmlSanitizer() {
        return new HtmlPolicyBuilder()
                // Allow basic text formatting
                .allowElements(
                        "p", "br", "hr",
                        "h1", "h2", "h3", "h4", "h5", "h6",
                        "strong", "b", "em", "i", "u", "s", "strike", "del",
                        "blockquote", "q", "cite"
                )
                // Allow lists
                .allowElements("ul", "ol", "li")
                // Allow code blocks and inline code
                .allowElements("pre", "code")
                .allowAttributes("class").onElements("pre", "code")
                // Allow tables
                .allowElements("table", "thead", "tbody", "tfoot", "tr", "th", "td")
                .allowAttributes("align").onElements("th", "td")
                // Allow links with safe protocols
                .allowElements("a")
                .allowAttributes("href").onElements("a")
                .allowUrlProtocols("http", "https", "mailto")
                .requireRelNofollowOnLinks()
                // Allow images with safe protocols
                .allowElements("img")
                .allowAttributes("src", "alt", "title", "width", "height").onElements("img")
                .allowUrlProtocols("http", "https")
                // Allow task list checkboxes
                .allowElements("input")
                .allowAttributes("type", "checked", "disabled").onElements("input")
                // Allow div and span for styling
                .allowElements("div", "span")
                .allowAttributes("class").onElements("div", "span")
                .toFactory();
    }
}

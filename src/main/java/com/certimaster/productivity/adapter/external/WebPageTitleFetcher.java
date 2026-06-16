package com.certimaster.productivity.adapter.external;

import com.certimaster.productivity.domain.service.ResourceKindDetector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.time.Duration;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Best-effort, lightweight fetch of a web page's {@code <title>} for link resources.
 * No thumbnails or OG parsing (kept deliberately light). On any failure — timeout,
 * non-HTML, parse miss — it falls back to the URL host so a resource always has a
 * sensible display title.
 */
@Component
public class WebPageTitleFetcher {

    private static final Logger log = LoggerFactory.getLogger(WebPageTitleFetcher.class);
    private static final Pattern TITLE_PATTERN =
            Pattern.compile("<title[^>]*>(.*?)</title>", Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
    private static final int MAX_TITLE_LENGTH = 500;

    private final RestClient restClient;

    public WebPageTitleFetcher() {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(Duration.ofSeconds(3));
        factory.setReadTimeout(Duration.ofSeconds(5));
        this.restClient = RestClient.builder()
                .requestFactory(factory)
                .defaultHeader("User-Agent", "CertiMaster-ResourceHub/1.0")
                .build();
    }

    /** Returns the page title, or the URL host as a fallback. Never throws. */
    public String fetchTitle(String url) {
        String fallback = ResourceKindDetector.hostOf(url);
        try {
            String body = restClient.get()
                    .uri(url)
                    .accept(MediaType.TEXT_HTML, MediaType.APPLICATION_XHTML_XML)
                    .retrieve()
                    .body(String.class);
            if (body == null) {
                return fallback;
            }
            Matcher matcher = TITLE_PATTERN.matcher(body);
            if (matcher.find()) {
                String title = matcher.group(1).replaceAll("\\s+", " ").trim();
                if (!title.isEmpty()) {
                    return title.length() > MAX_TITLE_LENGTH ? title.substring(0, MAX_TITLE_LENGTH) : title;
                }
            }
        } catch (Exception e) {
            log.debug("Title fetch failed for {}: {}", url, e.getMessage());
        }
        return fallback;
    }
}

package com.certimaster.productivity.domain.service;

import com.certimaster.productivity.domain.entity.ResourceKind;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;

class ResourceKindDetectorTest {

    @ParameterizedTest
    @CsvSource({
            "https://www.youtube.com/watch?v=abc, YOUTUBE",
            "https://youtu.be/abc,                YOUTUBE",
            "https://m.youtube.com/watch?v=abc,   YOUTUBE",
            "https://github.com/anthropics/claude, GITHUB",
            "https://gist.github.com/x,           GITHUB",
            "https://overreacted.io/some-post,    BLOG",
            "example.com/article,                 BLOG"
    })
    void detectsKindFromHost(String url, ResourceKind expected) {
        assertThat(ResourceKindDetector.detect(url)).isEqualTo(expected);
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"   "})
    void blankUrlIsNote(String url) {
        assertThat(ResourceKindDetector.detect(url)).isEqualTo(ResourceKind.NOTE);
    }

    @Test
    void hostStripsWww() {
        assertThat(ResourceKindDetector.hostOf("https://www.Example.COM/x")).isEqualTo("example.com");
    }

    @Test
    void hostAddsSchemeWhenMissing() {
        assertThat(ResourceKindDetector.hostOf("github.com/foo")).isEqualTo("github.com");
    }
}

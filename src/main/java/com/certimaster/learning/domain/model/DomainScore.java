package com.certimaster.learning.domain.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;

/**
 * The per-{@code CertificationDomain} breakdown within a {@link SessionScore}.
 *
 * <p>Persisted as jsonb on {@code user_exam_sessions.domain_scores}; the Jackson annotations
 * pin the serialized shape and must not change.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record DomainScore(Long domainId, String domainName, int totalQuestions, int correctCount, BigDecimal scorePercentage) {

    @JsonCreator
    public DomainScore(@JsonProperty("domainId") Long domainId,
                       @JsonProperty("domainName") String domainName,
                       @JsonProperty("totalQuestions") int totalQuestions,
                       @JsonProperty("correctCount") int correctCount,
                       @JsonProperty("scorePercentage") BigDecimal scorePercentage) {
        this.domainId = domainId;
        this.domainName = domainName;
        this.totalQuestions = totalQuestions;
        this.correctCount = correctCount;
        this.scorePercentage = scorePercentage;
    }
}

package com.certimaster.learning.adapter.web.response;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;

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

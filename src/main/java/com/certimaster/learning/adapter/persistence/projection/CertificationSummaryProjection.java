package com.certimaster.learning.adapter.persistence.projection;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public interface CertificationSummaryProjection {

    Long getId();

    LocalDateTime getCreatedAt();

    LocalDateTime getUpdatedAt();

    String getCreatedBy();

    String getUpdatedBy();

    String getName();

    String getCode();

    String getSlug();

    String getTagline();

    String getProvider();

    String getLevel();

    String getDifficultyLevel();

    String getDescription();

    String getOverview();

    Integer getDurationMinutes();

    Integer getTotalQuestions();

    BigDecimal getPassingScore();

    BigDecimal getPrice();

    BigDecimal getExamPriceUsd();

    Integer getAveragePrepWeeks();

    String getStatus();
}

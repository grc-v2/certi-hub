package com.certimaster.learning.domain.model;

import java.math.BigDecimal;
import java.util.List;

/**
 * The domain result of finishing a {@code UserExamSession}: the raw score, the percentage,
 * whether the attempt passed, and the per-{@code CertificationDomain} breakdown.
 */
public record SessionScore(BigDecimal score,
                           BigDecimal scorePercentage,
                           boolean passed,
                           List<DomainScore> domainScores) {
}

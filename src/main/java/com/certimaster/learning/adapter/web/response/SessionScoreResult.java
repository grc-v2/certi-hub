package com.certimaster.learning.adapter.web.response;

import java.math.BigDecimal;
import java.util.List;

public record SessionScoreResult(BigDecimal score, BigDecimal scorePercentage, boolean passed, List<DomainScore> domainScores) {

}

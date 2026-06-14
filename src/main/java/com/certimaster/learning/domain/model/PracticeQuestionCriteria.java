package com.certimaster.learning.domain.model;

import java.util.List;

/**
 * Domain input describing how to select questions for a PRACTICE session:
 * certification/bank scope, domain and difficulty filters, how many, and for whom.
 */
public record PracticeQuestionCriteria(Long certificationId, Long questionBankId, List<Long> domainIds, List<String> difficulties, int questionCount, Long userId) {

}

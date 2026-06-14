package com.certimaster.learning.domain.model;

import java.util.List;

/**
 * Domain input describing how to select questions from a question bank:
 * how many, with which domain and difficulty filters.
 */
public record QuestionSelectionCriteria(int questionCount, List<Long> domainIds, List<String> difficulties) {

}

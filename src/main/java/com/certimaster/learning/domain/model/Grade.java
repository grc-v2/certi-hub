package com.certimaster.learning.domain.model;

import java.util.List;

/**
 * The domain verdict produced when a {@code Question} evaluates a set of selected option ids:
 * whether the answer was correct, and the question's correct option ids.
 */
public record Grade(boolean correct, List<Long> correctOptionIds) {
}

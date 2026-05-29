package com.certimaster.learning.adapter.web.response;

/**
 * Performance breakdown by difficulty level.
 * Contains statistics for easy, medium, and hard questions.
 */
public record DifficultyAnalysis(
    DifficultyStats easy,
    DifficultyStats medium,
    DifficultyStats hard
) {}

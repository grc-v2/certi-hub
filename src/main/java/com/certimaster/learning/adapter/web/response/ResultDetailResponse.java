package com.certimaster.learning.adapter.web.response;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Complete exam result details including summary, topic performance, difficulty analysis,
 * strengths, weaknesses, recommendations, and earned badges.
 */
public record ResultDetailResponse(
    Long id,
    Long sessionId,
    Long userId,
    Long examId,
    String examTitle,
    Long certificationId,
    String certificationName,
    String examType,
    LocalDateTime completedAt,
    ResultSummary summary,
    List<TopicPerformanceItem> topicPerformance,
    DifficultyAnalysis difficultyAnalysis,
    List<String> strengths,
    List<String> weaknesses,
    List<String> nextSteps,
    List<Badge> badges,
    LocalDateTime createdAt
) {}

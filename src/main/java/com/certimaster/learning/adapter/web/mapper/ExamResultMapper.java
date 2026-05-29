package com.certimaster.learning.adapter.web.mapper;

import com.certimaster.learning.adapter.web.response.Badge;
import com.certimaster.learning.adapter.web.response.CorrectAnswerInfo;
import com.certimaster.learning.adapter.web.response.DifficultyAnalysis;
import com.certimaster.learning.adapter.web.response.DifficultyStats;
import com.certimaster.learning.adapter.web.response.HistoryItem;
import com.certimaster.learning.adapter.web.response.QuestionReportItem;
import com.certimaster.learning.adapter.web.response.QuestionSummary;
import com.certimaster.learning.adapter.web.response.ReferenceLink;
import com.certimaster.learning.adapter.web.response.ResultDetailResponse;
import com.certimaster.learning.adapter.web.response.ResultSummary;
import com.certimaster.learning.adapter.web.response.TopicPerformanceItem;
import com.certimaster.learning.adapter.web.response.UserAnswerInfo;
import com.certimaster.learning.domain.entity.ExamResult;
import com.certimaster.learning.domain.entity.QuestionResult;
import com.certimaster.learning.domain.entity.TopicPerformance;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Manual mapper for converting exam-result entities to DTOs.
 * Requirements: 1.1, 2.1
 */
@Component
public class ExamResultMapper {

    // Performance status thresholds
    private static final BigDecimal EXCELLENT_THRESHOLD = new BigDecimal("85");
    private static final BigDecimal GOOD_THRESHOLD = new BigDecimal("70");
    private static final BigDecimal AVERAGE_THRESHOLD = new BigDecimal("50");
    private static final int DEFAULT_PASSING_SCORE = 70;

    /**
     * Build complete ResultDetailResponse with all computed fields.
     * Requirements: 1.1
     */
    public ResultDetailResponse toResultDetailResponse(
            ExamResult examResult,
            List<TopicPerformanceItem> topicPerformance,
            DifficultyAnalysis difficultyAnalysis,
            List<String> strengths,
            List<String> weaknesses,
            List<String> nextSteps,
            List<Badge> badges) {

        ResultSummary summary = toResultSummary(examResult);

        return new ResultDetailResponse(
                examResult.getId(),
                examResult.getSessionId(),
                examResult.getUserId(),
                examResult.getExamId(),
                null, // examTitle - would need to fetch from exam-service
                examResult.getCertificationId(),
                null, // certificationName - would need to fetch from exam-service
                null, // examType - would need to fetch from exam-service
                examResult.getCompletedAt(),
                summary,
                topicPerformance,
                difficultyAnalysis,
                strengths,
                weaknesses,
                nextSteps,
                badges,
                examResult.getCreatedAt()
        );
    }

    /**
     * Map ExamResult to ResultSummary with calculated metrics.
     * Requirements: 1.1, 1.5
     */
    public ResultSummary toResultSummary(ExamResult examResult) {
        int totalQuestions = examResult.getTotalQuestions() != null ? examResult.getTotalQuestions() : 0;
        int correctAnswers = examResult.getCorrectAnswers() != null ? examResult.getCorrectAnswers() : 0;
        int wrongAnswers = examResult.getWrongAnswers() != null ? examResult.getWrongAnswers() : 0;
        int unanswered = examResult.getUnanswered() != null ? examResult.getUnanswered() : 0;
        int answeredQuestions = totalQuestions - unanswered;
        int totalTimeSpent = examResult.getTimeTakenSeconds() != null ? examResult.getTimeTakenSeconds() : 0;

        // Calculate average time per question (Requirements 1.5)
        int averageTimePerQuestion = 0;
        if (totalQuestions > 0) {
            averageTimePerQuestion = totalTimeSpent / totalQuestions;
        }

        return new ResultSummary(
                totalQuestions,
                answeredQuestions,
                unanswered,
                correctAnswers,
                wrongAnswers,
                correctAnswers, // score is the number of correct answers
                examResult.getPercentage(),
                DEFAULT_PASSING_SCORE,
                examResult.getPassStatus(),
                totalTimeSpent,
                averageTimePerQuestion
        );
    }

    /**
     * Map TopicPerformance entity to TopicPerformanceItem.
     * Requirements: 1.1
     */
    public TopicPerformanceItem toTopicPerformanceItem(TopicPerformance topicPerformance) {
        BigDecimal percentage = topicPerformance.getPercentage() != null ? 
                topicPerformance.getPercentage() : BigDecimal.ZERO;
        String status = calculatePerformanceStatus(percentage);
        List<String> recommendations = generateTopicRecommendations(
                topicPerformance.getTopicName(), percentage, status);

        return new TopicPerformanceItem(
                topicPerformance.getTopicId(),
                topicPerformance.getTopicName(),
                topicPerformance.getWeight(),
                topicPerformance.getTotalQuestions(),
                topicPerformance.getCorrectAnswers(),
                topicPerformance.getWrongAnswers(),
                percentage,
                status,
                recommendations
        );
    }

    /**
     * Map list of TopicPerformance entities to TopicPerformanceItem list.
     * Requirements: 1.1
     */
    public List<TopicPerformanceItem> toTopicPerformanceItems(List<TopicPerformance> topicPerformances) {
        if (topicPerformances == null) {
            return Collections.emptyList();
        }
        return topicPerformances.stream()
                .map(this::toTopicPerformanceItem)
                .collect(Collectors.toList());
    }


    /**
     * Map QuestionResult entity to QuestionReportItem.
     * Requirements: 2.1
     */
    public QuestionReportItem toQuestionReportItem(QuestionResult questionResult, int questionNumber) {
        // Build user answer info
        UserAnswerInfo userAnswer = new UserAnswerInfo(
                questionResult.getUserAnswerIds() != null ? 
                        Arrays.asList(questionResult.getUserAnswerIds()) : Collections.emptyList(),
                Collections.emptyList(), // selectedOptions text - would need to fetch from exam-service
                questionResult.getIsCorrect(),
                questionResult.getTimeSpentSeconds()
        );

        // Build correct answer info
        CorrectAnswerInfo correctAnswer = new CorrectAnswerInfo(
                questionResult.getCorrectAnswerIds() != null ? 
                        Arrays.asList(questionResult.getCorrectAnswerIds()) : Collections.emptyList(),
                Collections.emptyList() // correctOptions text - would need to fetch from exam-service
        );

        // Parse reference links
        List<ReferenceLink> referenceLinks = parseReferenceLinks(questionResult.getReferenceLinks());

        return new QuestionReportItem(
                questionResult.getQuestionId(),
                questionNumber,
                questionResult.getTopicId(),
                questionResult.getTopicName(),
                questionResult.getDifficulty(),
                questionResult.getQuestionType(),
                questionResult.getQuestionContent(),
                userAnswer,
                correctAnswer,
                questionResult.getExplanation(),
                referenceLinks,
                questionResult.getWasFlagged(),
                questionResult.getWasReviewed()
        );
    }

    /**
     * Map list of QuestionResult entities to QuestionReportItem list with sequential numbering.
     * Requirements: 2.1
     */
    public List<QuestionReportItem> toQuestionReportItems(List<QuestionResult> questionResults, int startNumber) {
        if (questionResults == null) {
            return Collections.emptyList();
        }
        List<QuestionReportItem> items = new ArrayList<>();
        int questionNumber = startNumber;
        for (QuestionResult qr : questionResults) {
            items.add(toQuestionReportItem(qr, questionNumber++));
        }
        return items;
    }

    /**
     * Build DifficultyAnalysis from difficulty statistics.
     * Requirements: 1.1
     */
    public DifficultyAnalysis toDifficultyAnalysis(List<Object[]> difficultyStats) {
        Map<String, DifficultyStats> statsMap = new HashMap<>();
        statsMap.put("EASY", new DifficultyStats(0, 0, BigDecimal.ZERO));
        statsMap.put("MEDIUM", new DifficultyStats(0, 0, BigDecimal.ZERO));
        statsMap.put("HARD", new DifficultyStats(0, 0, BigDecimal.ZERO));

        if (difficultyStats != null) {
            for (Object[] row : difficultyStats) {
                String difficulty = (String) row[0];
                if (difficulty == null) continue;

                Long total = (Long) row[1];
                Long correct = (Long) row[2];

                int totalInt = total != null ? total.intValue() : 0;
                int correctInt = correct != null ? correct.intValue() : 0;

                BigDecimal percentage = BigDecimal.ZERO;
                if (totalInt > 0) {
                    percentage = BigDecimal.valueOf(correctInt)
                            .multiply(BigDecimal.valueOf(100))
                            .divide(BigDecimal.valueOf(totalInt), 2, RoundingMode.HALF_UP);
                }

                statsMap.put(difficulty.toUpperCase(), new DifficultyStats(totalInt, correctInt, percentage));
            }
        }

        return new DifficultyAnalysis(
                statsMap.get("EASY"),
                statsMap.get("MEDIUM"),
                statsMap.get("HARD")
        );
    }

    /**
     * Build QuestionSummary from question results.
     * Requirements: 2.5
     */
    public QuestionSummary toQuestionSummary(List<QuestionResult> questionResults) {
        if (questionResults == null || questionResults.isEmpty()) {
            return new QuestionSummary(0, 0, 0, 0, 0);
        }

        int total = questionResults.size();
        int correct = (int) questionResults.stream()
                .filter(q -> Boolean.TRUE.equals(q.getIsCorrect())).count();
        int wrong = (int) questionResults.stream()
                .filter(q -> Boolean.FALSE.equals(q.getIsCorrect())).count();
        int flagged = (int) questionResults.stream()
                .filter(q -> Boolean.TRUE.equals(q.getWasFlagged())).count();
        int reviewed = (int) questionResults.stream()
                .filter(q -> Boolean.TRUE.equals(q.getWasReviewed())).count();

        return new QuestionSummary(total, correct, wrong, flagged, reviewed);
    }

    /**
     * Map ExamResult to HistoryItem.
     * Requirements: 3.1
     */
    public HistoryItem toHistoryItem(ExamResult examResult, int attemptNumber) {
        return new HistoryItem(
                examResult.getId(),
                examResult.getExamId(),
                null, // examTitle - would need to fetch from exam-service
                null, // examType - would need to fetch from exam-service
                examResult.getCertificationId(),
                null, // certificationName - would need to fetch from exam-service
                examResult.getCorrectAnswers(),
                examResult.getPercentage(),
                examResult.getPassStatus(),
                examResult.getTotalQuestions(),
                examResult.getCorrectAnswers(),
                examResult.getTimeTakenSeconds(),
                examResult.getCompletedAt(),
                attemptNumber
        );
    }


    /**
     * Generate strengths based on topic performance.
     */
    public List<String> generateStrengths(List<TopicPerformanceItem> topicPerformanceItems) {
        if (topicPerformanceItems == null) {
            return Collections.emptyList();
        }
        return topicPerformanceItems.stream()
                .filter(tp -> "EXCELLENT".equals(tp.status()) || "GOOD".equals(tp.status()))
                .sorted((a, b) -> b.percentage().compareTo(a.percentage()))
                .limit(3)
                .map(tp -> String.format("Strong performance in %s (%.1f%%)", tp.topicName(), tp.percentage()))
                .collect(Collectors.toList());
    }

    /**
     * Generate weaknesses based on topic performance.
     */
    public List<String> generateWeaknesses(List<TopicPerformanceItem> topicPerformanceItems) {
        if (topicPerformanceItems == null) {
            return Collections.emptyList();
        }
        return topicPerformanceItems.stream()
                .filter(tp -> "POOR".equals(tp.status()) || "AVERAGE".equals(tp.status()))
                .sorted(Comparator.comparing(TopicPerformanceItem::percentage))
                .limit(3)
                .map(tp -> String.format("Needs improvement in %s (%.1f%%)", tp.topicName(), tp.percentage()))
                .collect(Collectors.toList());
    }

    /**
     * Generate next steps/recommendations.
     */
    public List<String> generateNextSteps(List<TopicPerformanceItem> topicPerformanceItems, ResultSummary summary) {
        List<String> nextSteps = new ArrayList<>();

        if (topicPerformanceItems != null) {
            // Find weakest topics
            List<TopicPerformanceItem> weakTopics = topicPerformanceItems.stream()
                    .filter(tp -> "POOR".equals(tp.status()))
                    .sorted(Comparator.comparing(TopicPerformanceItem::percentage))
                    .limit(2)
                    .toList();

            for (TopicPerformanceItem weakTopic : weakTopics) {
                nextSteps.add(String.format("Focus on studying %s - review fundamentals and practice more questions", 
                        weakTopic.topicName()));
            }
        }

        // Add general recommendations based on overall performance
        if (summary != null && "FAILED".equals(summary.status())) {
            nextSteps.add("Review all incorrect answers and understand the explanations");
            nextSteps.add("Take practice exams to build confidence before retaking");
        } else {
            nextSteps.add("Maintain your strong areas while improving weaker topics");
        }

        return nextSteps;
    }

    /**
     * Generate badges based on performance.
     */
    public List<Badge> generateBadges(ExamResult examResult, ResultSummary summary) {
        List<Badge> badges = new ArrayList<>();

        if (examResult == null || summary == null) {
            return badges;
        }

        // Pass badge
        if ("PASSED".equals(examResult.getPassStatus())) {
            badges.add(new Badge("PASS", "Exam Passed", "Successfully passed the exam", "trophy"));
        }

        // Perfect score badge
        if (summary.percentage() != null && summary.percentage().compareTo(BigDecimal.valueOf(100)) == 0) {
            badges.add(new Badge("PERFECT", "Perfect Score", "Achieved 100% accuracy", "star"));
        }

        // High achiever badge (90%+)
        if (summary.percentage() != null && summary.percentage().compareTo(BigDecimal.valueOf(90)) >= 0) {
            badges.add(new Badge("HIGH_ACHIEVER", "High Achiever", "Scored 90% or above", "medal"));
        }

        // Speed demon badge (completed in less than half the time)
        if (summary.averageTimePerQuestion() != null && summary.averageTimePerQuestion() < 30) {
            badges.add(new Badge("SPEED", "Quick Thinker", "Completed questions quickly", "lightning"));
        }

        return badges;
    }

    /**
     * Calculate performance status based on percentage.
     */
    public String calculatePerformanceStatus(BigDecimal percentage) {
        if (percentage == null) {
            return "POOR";
        }
        if (percentage.compareTo(EXCELLENT_THRESHOLD) >= 0) {
            return "EXCELLENT";
        } else if (percentage.compareTo(GOOD_THRESHOLD) >= 0) {
            return "GOOD";
        } else if (percentage.compareTo(AVERAGE_THRESHOLD) >= 0) {
            return "AVERAGE";
        } else {
            return "POOR";
        }
    }

    /**
     * Generate topic-specific recommendations.
     */
    public List<String> generateTopicRecommendations(String topicName, BigDecimal percentage, String status) {
        List<String> recommendations = new ArrayList<>();

        if (status == null) {
            return recommendations;
        }

        switch (status) {
            case "POOR" -> {
                recommendations.add(String.format("Review %s fundamentals thoroughly", topicName));
                recommendations.add("Practice more questions in this area");
            }
            case "AVERAGE" -> {
                recommendations.add(String.format("Continue practicing %s questions", topicName));
                recommendations.add("Focus on understanding concepts you got wrong");
            }
            case "GOOD" -> {
                recommendations.add("Good progress! Review any mistakes to reach excellence");
            }
            case "EXCELLENT" -> {
                recommendations.add("Excellent! Maintain this level of understanding");
            }
        }

        return recommendations;
    }

    /**
     * Parse reference links from stored string format.
     * Format: "title1|url1;title2|url2"
     */
    public List<ReferenceLink> parseReferenceLinks(String referenceLinksStr) {
        if (referenceLinksStr == null || referenceLinksStr.isBlank()) {
            return Collections.emptyList();
        }

        List<ReferenceLink> links = new ArrayList<>();
        String[] linkParts = referenceLinksStr.split(";");
        for (String linkPart : linkParts) {
            String[] parts = linkPart.split("\\|");
            if (parts.length >= 2) {
                links.add(new ReferenceLink(parts[0].trim(), parts[1].trim()));
            } else if (parts.length == 1) {
                links.add(new ReferenceLink("Reference", parts[0].trim()));
            }
        }
        return links;
    }
}

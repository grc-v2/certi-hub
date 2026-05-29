package com.certimaster.learning.domain.service;

import com.certimaster.learning.adapter.web.response.Badge;
import com.certimaster.learning.adapter.web.response.ResultSummary;
import com.certimaster.learning.adapter.web.response.TopicPerformanceItem;
import com.certimaster.learning.domain.entity.ExamResult;
import com.certimaster.learning.domain.entity.TopicPerformance;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Component
public class ResultInsightService {

    private static final BigDecimal EXCELLENT_THRESHOLD = new BigDecimal("85");
    private static final BigDecimal GOOD_THRESHOLD = new BigDecimal("70");
    private static final BigDecimal AVERAGE_THRESHOLD = new BigDecimal("50");

    public List<TopicPerformanceItem> buildTopicPerformanceItems(List<TopicPerformance> topicPerformances) {
        return topicPerformances.stream()
                .map(topicPerformance -> {
                    BigDecimal percentage = topicPerformance.getPercentage() != null
                            ? topicPerformance.getPercentage()
                            : BigDecimal.ZERO;
                    String status = calculatePerformanceStatus(percentage);
                    List<String> recommendations = generateTopicRecommendations(
                            topicPerformance.getTopicName(),
                            status
                    );

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
                })
                .toList();
    }

    public List<String> generateStrengths(List<TopicPerformanceItem> topicPerformanceItems) {
        return topicPerformanceItems.stream()
                .filter(topicPerformance -> "EXCELLENT".equals(topicPerformance.status()) || "GOOD".equals(topicPerformance.status()))
                .sorted((left, right) -> right.percentage().compareTo(left.percentage()))
                .limit(3)
                .map(topicPerformance -> String.format("Strong performance in %s (%.1f%%)",
                        topicPerformance.topicName(),
                        topicPerformance.percentage()))
                .toList();
    }

    public List<String> generateWeaknesses(List<TopicPerformanceItem> topicPerformanceItems) {
        return topicPerformanceItems.stream()
                .filter(topicPerformance -> "POOR".equals(topicPerformance.status()) || "AVERAGE".equals(topicPerformance.status()))
                .sorted(Comparator.comparing(TopicPerformanceItem::percentage))
                .limit(3)
                .map(topicPerformance -> String.format("Needs improvement in %s (%.1f%%)",
                        topicPerformance.topicName(),
                        topicPerformance.percentage()))
                .toList();
    }

    public List<String> generateNextSteps(List<TopicPerformanceItem> topicPerformanceItems, ResultSummary summary) {
        List<String> nextSteps = new ArrayList<>();

        List<TopicPerformanceItem> weakTopics = topicPerformanceItems.stream()
                .filter(topicPerformance -> "POOR".equals(topicPerformance.status()))
                .sorted(Comparator.comparing(TopicPerformanceItem::percentage))
                .limit(2)
                .toList();

        for (TopicPerformanceItem weakTopic : weakTopics) {
            nextSteps.add(String.format("Focus on studying %s - review fundamentals and practice more questions", weakTopic.topicName()));
        }

        if ("FAILED".equals(summary.status())) {
            nextSteps.add("Review all incorrect answers and understand the explanations");
            nextSteps.add("Take practice exams to build confidence before retaking");
        } else {
            nextSteps.add("Maintain your strong areas while improving weaker topics");
        }

        return nextSteps;
    }

    public List<Badge> generateBadges(ExamResult examResult, ResultSummary summary) {
        List<Badge> badges = new ArrayList<>();

        if ("PASSED".equals(examResult.getPassStatus())) {
            badges.add(new Badge("PASS", "Exam Passed", "Successfully passed the exam", "trophy"));
        }

        if (summary.percentage().compareTo(BigDecimal.valueOf(100)) == 0) {
            badges.add(new Badge("PERFECT", "Perfect Score", "Achieved 100% accuracy", "star"));
        }

        if (summary.percentage().compareTo(BigDecimal.valueOf(90)) >= 0) {
            badges.add(new Badge("HIGH_ACHIEVER", "High Achiever", "Scored 90% or above", "medal"));
        }

        if (summary.averageTimePerQuestion() != null && summary.averageTimePerQuestion() < 30) {
            badges.add(new Badge("SPEED", "Quick Thinker", "Completed questions quickly", "lightning"));
        }

        return badges;
    }

    private String calculatePerformanceStatus(BigDecimal percentage) {
        if (percentage.compareTo(EXCELLENT_THRESHOLD) >= 0) {
            return "EXCELLENT";
        }
        if (percentage.compareTo(GOOD_THRESHOLD) >= 0) {
            return "GOOD";
        }
        if (percentage.compareTo(AVERAGE_THRESHOLD) >= 0) {
            return "AVERAGE";
        }
        return "POOR";
    }

    private List<String> generateTopicRecommendations(String topicName, String status) {
        List<String> recommendations = new ArrayList<>();

        switch (status) {
            case "POOR" -> {
                recommendations.add(String.format("Review %s fundamentals thoroughly", topicName));
                recommendations.add("Practice more questions in this area");
            }
            case "AVERAGE" -> {
                recommendations.add(String.format("Continue practicing %s questions", topicName));
                recommendations.add("Focus on understanding concepts you got wrong");
            }
            case "GOOD" -> recommendations.add("Good progress! Review any mistakes to reach excellence");
            case "EXCELLENT" -> recommendations.add("Excellent! Maintain this level of understanding");
        }

        return recommendations;
    }
}

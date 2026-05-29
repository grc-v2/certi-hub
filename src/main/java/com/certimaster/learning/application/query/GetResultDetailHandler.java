package com.certimaster.learning.application.query;

import com.certimaster.shared.exception.ResourceNotFoundException;
import com.certimaster.learning.application.query.GetResultDetailQuery;
import com.certimaster.learning.adapter.web.response.Badge;
import com.certimaster.learning.adapter.web.response.CertificationInfo;
import com.certimaster.learning.adapter.web.response.DifficultyAnalysis;
import com.certimaster.learning.adapter.web.response.DifficultyStats;
import com.certimaster.learning.adapter.web.response.ExamInfo;
import com.certimaster.learning.adapter.web.response.ResultDetailResponse;
import com.certimaster.learning.adapter.web.response.ResultSummary;
import com.certimaster.learning.adapter.web.response.TopicPerformanceItem;
import com.certimaster.learning.domain.entity.ExamResult;
import com.certimaster.learning.domain.entity.TopicPerformance;
import com.certimaster.learning.adapter.persistence.ExamResultQueryRepository;
import com.certimaster.learning.adapter.persistence.QuestionResultRepository;
import com.certimaster.learning.adapter.persistence.TopicPerformanceRepository;
import com.certimaster.learning.domain.service.ExamResultReferenceDataService;
import com.certimaster.learning.domain.service.ResultAuthorizationService;
import com.certimaster.learning.domain.service.ResultInsightService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
class GetResultDetailHandler {

    private static final Logger log = LoggerFactory.getLogger(GetResultDetailHandler.class);
    private static final int FALLBACK_PASSING_SCORE = 70;

    private final ExamResultQueryRepository examResultQueryRepository;
    private final QuestionResultRepository questionResultRepository;
    private final TopicPerformanceRepository topicPerformanceRepository;
    private final ExamResultReferenceDataService referenceDataService;
    private final ResultAuthorizationService authorizationService;
    private final ResultInsightService insightService;

    GetResultDetailHandler(ExamResultQueryRepository examResultQueryRepository,
                           QuestionResultRepository questionResultRepository,
                           TopicPerformanceRepository topicPerformanceRepository,
                           ExamResultReferenceDataService referenceDataService,
                           ResultAuthorizationService authorizationService,
                           ResultInsightService insightService) {
        this.examResultQueryRepository = examResultQueryRepository;
        this.questionResultRepository = questionResultRepository;
        this.topicPerformanceRepository = topicPerformanceRepository;
        this.referenceDataService = referenceDataService;
        this.authorizationService = authorizationService;
        this.insightService = insightService;
    }

    @Transactional(readOnly = true)
    ResultDetailResponse handle(GetResultDetailQuery query) {
        log.debug("Getting result detail for resultId={}, currentUserId={}", query.resultId(), query.currentUserId());

        ExamResult examResult = examResultQueryRepository.findById(query.resultId())
                .orElseThrow(() -> ResourceNotFoundException.byId("ExamResult", query.resultId()));

        authorizationService.validateUserAuthorization(examResult.getUserId(), query.currentUserId());

        ExamInfo examInfo = examResult.getExamId() != null
                ? referenceDataService.getExam(examResult.getExamId()).orElse(null)
                : null;
        CertificationInfo certificationInfo = examResult.getCertificationId() != null
                ? referenceDataService.getCertification(examResult.getCertificationId()).orElse(null)
                : null;

        ResultSummary summary = buildResultSummary(examResult);

        List<TopicPerformance> topicPerformances = topicPerformanceRepository.findByExamResultId(query.resultId());
        List<TopicPerformanceItem> topicPerformanceItems = insightService.buildTopicPerformanceItems(topicPerformances);

        DifficultyAnalysis difficultyAnalysis = buildDifficultyAnalysis(query.resultId());

        List<String> strengths = insightService.generateStrengths(topicPerformanceItems);
        List<String> weaknesses = insightService.generateWeaknesses(topicPerformanceItems);
        List<String> nextSteps = insightService.generateNextSteps(topicPerformanceItems, summary);
        List<Badge> badges = insightService.generateBadges(examResult, summary);

        return new ResultDetailResponse(
                examResult.getId(),
                examResult.getSessionId(),
                examResult.getUserId(),
                examResult.getExamId(),
                examInfo != null ? examInfo.getTitle() : null,
                examResult.getCertificationId(),
                certificationInfo != null ? certificationInfo.getName() : null,
                examInfo != null ? examInfo.getType() : null,
                examResult.getCompletedAt(),
                summary,
                topicPerformanceItems,
                difficultyAnalysis,
                strengths,
                weaknesses,
                nextSteps,
                badges,
                examResult.getCreatedAt()
        );
    }

    private ResultSummary buildResultSummary(ExamResult examResult) {
        int totalQuestions = examResult.getTotalQuestions() != null ? examResult.getTotalQuestions() : 0;
        int correctAnswers = examResult.getCorrectAnswers() != null ? examResult.getCorrectAnswers() : 0;
        int wrongAnswers = examResult.getWrongAnswers() != null ? examResult.getWrongAnswers() : 0;
        int unanswered = examResult.getUnanswered() != null ? examResult.getUnanswered() : 0;
        int answeredQuestions = totalQuestions - unanswered;
        int totalTimeSpent = examResult.getTimeTakenSeconds() != null ? examResult.getTimeTakenSeconds() : 0;
        int averageTimePerQuestion = totalQuestions > 0 ? totalTimeSpent / totalQuestions : 0;

        return new ResultSummary(
                totalQuestions,
                answeredQuestions,
                unanswered,
                correctAnswers,
                wrongAnswers,
                correctAnswers,
                examResult.getPercentage(),
                examResult.getPassingScore() != null ? examResult.getPassingScore() : FALLBACK_PASSING_SCORE,
                examResult.getPassStatus(),
                totalTimeSpent,
                averageTimePerQuestion
        );
    }

    private DifficultyAnalysis buildDifficultyAnalysis(Long resultId) {
        List<Object[]> difficultyStats = questionResultRepository.countByDifficultyForResult(resultId);

        Map<String, DifficultyStats> statsMap = new HashMap<>();
        statsMap.put("EASY", new DifficultyStats(0, 0, BigDecimal.ZERO));
        statsMap.put("MEDIUM", new DifficultyStats(0, 0, BigDecimal.ZERO));
        statsMap.put("HARD", new DifficultyStats(0, 0, BigDecimal.ZERO));

        for (Object[] row : difficultyStats) {
            String difficulty = (String) row[0];
            if (difficulty == null) {
                continue;
            }

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

        return new DifficultyAnalysis(
                statsMap.get("EASY"),
                statsMap.get("MEDIUM"),
                statsMap.get("HARD")
        );
    }
}

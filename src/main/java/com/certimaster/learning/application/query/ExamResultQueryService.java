package com.certimaster.learning.application.query;

import com.certimaster.shared.dto.PageResponse;
import com.certimaster.shared.exception.ResourceNotFoundException;
import com.certimaster.learning.adapter.web.response.Badge;
import com.certimaster.learning.adapter.web.response.CertificationStats;
import com.certimaster.learning.adapter.web.response.CorrectAnswerInfo;
import com.certimaster.learning.adapter.web.response.DifficultyAnalysis;
import com.certimaster.learning.adapter.web.response.DifficultyStats;
import com.certimaster.learning.adapter.web.response.HistoryItem;
import com.certimaster.learning.adapter.web.response.HistoryStatistics;
import com.certimaster.learning.adapter.web.response.QuestionReportItem;
import com.certimaster.learning.adapter.web.response.QuestionReportResponse;
import com.certimaster.learning.adapter.web.response.QuestionSummary;
import com.certimaster.learning.adapter.web.response.ReferenceLink;
import com.certimaster.learning.adapter.web.response.ResultDetailResponse;
import com.certimaster.learning.adapter.web.response.ResultSummary;
import com.certimaster.learning.adapter.web.response.TopicPerformanceItem;
import com.certimaster.learning.adapter.web.response.UserAnswerInfo;
import com.certimaster.learning.adapter.web.response.UserHistoryResponse;
import com.certimaster.learning.domain.model.CertificationInfo;
import com.certimaster.learning.domain.model.ExamInfo;
import com.certimaster.learning.domain.entity.ExamResult;
import com.certimaster.learning.domain.entity.QuestionResult;
import com.certimaster.learning.domain.entity.TopicPerformance;
import com.certimaster.learning.adapter.persistence.ExamResultQueryRepository;
import com.certimaster.learning.adapter.persistence.QuestionResultRepository;
import com.certimaster.learning.adapter.persistence.TopicPerformanceRepository;
import com.certimaster.learning.domain.service.ExamResultReferenceDataService;
import com.certimaster.learning.domain.service.ResultAuthorizationService;
import com.certimaster.learning.adapter.web.mapper.ResultInsightService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Read side for the {@code ExamResult} aggregate: a single result detail, the paged
 * per-question report, and a user's exam history. Holds its query verbs directly — one
 * query service per aggregate, matching every other read service in the platform.
 */
@Service
public class ExamResultQueryService {

    private static final int FALLBACK_PASSING_SCORE = 70;

    private final ExamResultQueryRepository examResultQueryRepository;
    private final QuestionResultRepository questionResultRepository;
    private final TopicPerformanceRepository topicPerformanceRepository;
    private final ExamResultReferenceDataService referenceDataService;
    private final ResultAuthorizationService authorizationService;
    private final ResultInsightService insightService;

    public ExamResultQueryService(ExamResultQueryRepository examResultQueryRepository,
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

    // ──────────────────────────── Result detail ────────────────────────────

    @Transactional(readOnly = true)
    public ResultDetailResponse getResultDetail(GetResultDetailQuery query) {
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

    // ──────────────────────────── Question report ────────────────────────────

    @Transactional(readOnly = true)
    public QuestionReportResponse getQuestionReport(GetQuestionReportQuery query) {
        ExamResult examResult = examResultQueryRepository.findById(query.resultId())
                .orElseThrow(() -> ResourceNotFoundException.byId("ExamResult", query.resultId()));

        authorizationService.validateUserAuthorization(examResult.getUserId(), query.currentUserId());

        int page = query.page() != null ? query.page() : 0;
        int size = query.size() != null ? query.size() : 10;
        Pageable pageable = PageRequest.of(page, size);

        Boolean isCorrect = null;
        Boolean wasFlagged = null;
        String filter = query.filter() != null ? query.filter() : "ALL";

        switch (filter) {
            case "CORRECT" -> isCorrect = true;
            case "WRONG" -> isCorrect = false;
            case "FLAGGED" -> wasFlagged = true;
        }

        Page<QuestionResult> questionResultPage = questionResultRepository.searchByFilters(
                query.resultId(),
                isCorrect,
                query.topicId(),
                query.difficulty(),
                wasFlagged,
                pageable
        );

        List<QuestionReportItem> questionItems = new ArrayList<>();
        int questionNumber = page * size + 1;
        for (QuestionResult questionResult : questionResultPage.getContent()) {
            questionItems.add(buildQuestionReportItem(questionResult, questionNumber++));
        }

        QuestionSummary summary = buildQuestionSummary(query.resultId());

        PageResponse<?> pagination = PageResponse.builder()
                .pageNumber(questionResultPage.getNumber())
                .pageSize(questionResultPage.getSize())
                .totalElements(questionResultPage.getTotalElements())
                .totalPages(questionResultPage.getTotalPages())
                .first(questionResultPage.isFirst())
                .last(questionResultPage.isLast())
                .empty(questionResultPage.isEmpty())
                .build();

        return new QuestionReportResponse(query.resultId(), questionItems, summary, pagination);
    }

    private QuestionReportItem buildQuestionReportItem(QuestionResult questionResult, int questionNumber) {
        UserAnswerInfo userAnswer = new UserAnswerInfo(
                questionResult.getUserAnswerIds() != null
                        ? Arrays.asList(questionResult.getUserAnswerIds())
                        : Collections.emptyList(),
                Collections.emptyList(),
                questionResult.getIsCorrect(),
                questionResult.getTimeSpentSeconds()
        );

        CorrectAnswerInfo correctAnswer = new CorrectAnswerInfo(
                questionResult.getCorrectAnswerIds() != null
                        ? Arrays.asList(questionResult.getCorrectAnswerIds())
                        : Collections.emptyList(),
                Collections.emptyList()
        );

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

    private List<ReferenceLink> parseReferenceLinks(String referenceLinksStr) {
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

    private QuestionSummary buildQuestionSummary(Long resultId) {
        List<QuestionResult> allQuestions = questionResultRepository.findByExamResultId(resultId);

        int total = allQuestions.size();
        int correct = (int) allQuestions.stream().filter(question -> Boolean.TRUE.equals(question.getIsCorrect())).count();
        int wrong = (int) allQuestions.stream().filter(question -> Boolean.FALSE.equals(question.getIsCorrect())).count();
        int flagged = (int) allQuestions.stream().filter(question -> Boolean.TRUE.equals(question.getWasFlagged())).count();
        int reviewed = (int) allQuestions.stream().filter(question -> Boolean.TRUE.equals(question.getWasReviewed())).count();

        return new QuestionSummary(total, correct, wrong, flagged, reviewed);
    }

    // ──────────────────────────── User history ────────────────────────────

    @Transactional(readOnly = true)
    public UserHistoryResponse getUserHistory(GetUserHistoryQuery query) {
        authorizationService.validateUserAuthorization(query.userId(), query.currentUserId());

        int page = query.page() != null ? query.page() : 0;
        int size = query.size() != null ? query.size() : 10;
        Sort sort = parseSort(query.sort());
        Pageable pageable = PageRequest.of(page, size, sort);

        String status = normalizeStatus(query.status());

        Page<ExamResult> resultPage;
        if (query.startDate() != null || query.endDate() != null) {
            resultPage = examResultQueryRepository.searchWithDateRange(
                    query.userId(),
                    query.certificationId(),
                    query.examId(),
                    status,
                    query.startDate(),
                    query.endDate(),
                    pageable
            );
        } else {
            resultPage = examResultQueryRepository.search(
                    query.userId(),
                    query.certificationId(),
                    query.examId(),
                    status,
                    pageable
            );
        }

        List<HistoryItem> historyItems = buildHistoryItems(resultPage.getContent(), query.userId());
        HistoryStatistics statistics = calculateHistoryStatistics(query);

        PageResponse<?> pagination = PageResponse.builder()
                .pageNumber(resultPage.getNumber())
                .pageSize(resultPage.getSize())
                .totalElements(resultPage.getTotalElements())
                .totalPages(resultPage.getTotalPages())
                .first(resultPage.isFirst())
                .last(resultPage.isLast())
                .empty(resultPage.isEmpty())
                .build();

        return new UserHistoryResponse(query.userId(), historyItems, statistics, pagination);
    }

    private Sort parseSort(String sortStr) {
        if (sortStr == null || sortStr.isBlank()) {
            return Sort.by(Sort.Direction.DESC, "completedAt");
        }

        String[] parts = sortStr.split(",");
        String field = parts[0].trim();
        Sort.Direction direction = Sort.Direction.DESC;

        if (parts.length > 1 && "asc".equalsIgnoreCase(parts[1].trim())) {
            direction = Sort.Direction.ASC;
        }

        return Sort.by(direction, field);
    }

    private List<HistoryItem> buildHistoryItems(List<ExamResult> results, Long userId) {
        Map<Long, Integer> resultAttemptNumbers = new HashMap<>();
        for (Object[] row : examResultQueryRepository.findAttemptNumbersByUserId(userId)) {
            Long id = ((Number) row[0]).longValue();
            Integer attempt = ((Number) row[1]).intValue();
            resultAttemptNumbers.put(id, attempt);
        }

        Set<Long> examIds = results.stream()
                .map(ExamResult::getExamId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
        Set<Long> certificationIds = results.stream()
                .map(ExamResult::getCertificationId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        Map<Long, ExamInfo> examMap = examIds.isEmpty()
                ? Collections.emptyMap()
                : referenceDataService.getExams(examIds);
        Map<Long, CertificationInfo> certificationMap = certificationIds.isEmpty()
                ? Collections.emptyMap()
                : referenceDataService.getCertifications(certificationIds);

        return results.stream()
                .map(examResult -> {
                    ExamInfo examInfo = examResult.getExamId() != null ? examMap.get(examResult.getExamId()) : null;
                    CertificationInfo certificationInfo = examResult.getCertificationId() != null
                            ? certificationMap.get(examResult.getCertificationId())
                            : null;
                    return new HistoryItem(
                            examResult.getId(),
                            examResult.getExamId(),
                            examInfo != null ? examInfo.getTitle() : null,
                            examInfo != null ? examInfo.getType() : null,
                            examResult.getCertificationId(),
                            certificationInfo != null ? certificationInfo.getName() : null,
                            examResult.getCorrectAnswers(),
                            examResult.getPercentage(),
                            examResult.getPassStatus(),
                            examResult.getTotalQuestions(),
                            examResult.getCorrectAnswers(),
                            examResult.getTimeTakenSeconds(),
                            examResult.getCompletedAt(),
                            resultAttemptNumbers.getOrDefault(examResult.getId(), 1)
                    );
                })
                .toList();
    }

    private HistoryStatistics calculateHistoryStatistics(GetUserHistoryQuery query) {
        String status = normalizeStatus(query.status());
        List<Object[]> aggRows = examResultQueryRepository.aggregateHistoryStats(
                query.userId(),
                query.certificationId(),
                query.examId(),
                status,
                query.startDate(),
                query.endDate()
        );
        Object[] agg = aggRows.isEmpty() ? null : aggRows.getFirst();

        int totalExams = agg != null && agg[0] != null ? ((Number) agg[0]).intValue() : 0;
        int totalPassed = agg != null && agg[1] != null ? ((Number) agg[1]).intValue() : 0;
        int totalFailed = totalExams - totalPassed;

        BigDecimal averageScore = agg != null && agg[2] != null
                ? BigDecimal.valueOf(((Number) agg[2]).doubleValue()).setScale(2, RoundingMode.HALF_UP)
                : BigDecimal.ZERO;
        BigDecimal bestScore = agg != null && agg[3] != null
                ? BigDecimal.valueOf(((Number) agg[3]).doubleValue()).setScale(2, RoundingMode.HALF_UP)
                : BigDecimal.ZERO;
        int totalTimeSpent = agg != null && agg[4] != null ? ((Number) agg[4]).intValue() : 0;

        BigDecimal passRate = BigDecimal.ZERO;
        if (totalExams > 0) {
            passRate = BigDecimal.valueOf(totalPassed)
                    .multiply(BigDecimal.valueOf(100))
                    .divide(BigDecimal.valueOf(totalExams), 2, RoundingMode.HALF_UP);
        }

        int averageTimePerExam = totalExams > 0 ? totalTimeSpent / totalExams : 0;
        List<CertificationStats> certificationStats = calculateCertificationStats(query);

        return new HistoryStatistics(
                totalExams,
                totalPassed,
                totalFailed,
                passRate,
                averageScore,
                bestScore,
                totalTimeSpent,
                averageTimePerExam,
                certificationStats
        );
    }

    private List<CertificationStats> calculateCertificationStats(GetUserHistoryQuery query) {
        String status = normalizeStatus(query.status());
        List<Object[]> rows = examResultQueryRepository.aggregateHistoryStatsByCertification(
                query.userId(),
                query.certificationId(),
                query.examId(),
                status,
                query.startDate(),
                query.endDate()
        );

        if (rows.isEmpty()) {
            return Collections.emptyList();
        }

        Set<Long> certificationIds = rows.stream()
                .map(row -> row[0])
                .filter(Objects::nonNull)
                .map(value -> ((Number) value).longValue())
                .collect(Collectors.toSet());
        Map<Long, CertificationInfo> certificationMap = certificationIds.isEmpty()
                ? Collections.emptyMap()
                : referenceDataService.getCertifications(certificationIds);

        return rows.stream()
                .filter(row -> row[0] != null)
                .map(row -> {
                    Long certificationId = ((Number) row[0]).longValue();
                    int exams = ((Number) row[1]).intValue();
                    BigDecimal avgScore = row[2] != null
                            ? BigDecimal.valueOf(((Number) row[2]).doubleValue()).setScale(2, RoundingMode.HALF_UP)
                            : BigDecimal.ZERO;
                    CertificationInfo certificationInfo = certificationMap.get(certificationId);
                    return new CertificationStats(
                            certificationId,
                            certificationInfo != null ? certificationInfo.getName() : null,
                            exams,
                            avgScore
                    );
                })
                .toList();
    }

    private String normalizeStatus(String status) {
        return status != null && !status.isBlank() ? status : null;
    }
}

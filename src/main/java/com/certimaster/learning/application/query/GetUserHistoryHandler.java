package com.certimaster.learning.application.query;

import com.certimaster.shared.dto.PageResponse;
import com.certimaster.learning.application.query.GetUserHistoryQuery;
import com.certimaster.learning.domain.model.CertificationInfo;
import com.certimaster.learning.adapter.web.response.CertificationStats;
import com.certimaster.learning.domain.model.ExamInfo;
import com.certimaster.learning.adapter.web.response.HistoryItem;
import com.certimaster.learning.adapter.web.response.HistoryStatistics;
import com.certimaster.learning.adapter.web.response.UserHistoryResponse;
import com.certimaster.learning.domain.entity.ExamResult;
import com.certimaster.learning.adapter.persistence.ExamResultQueryRepository;
import com.certimaster.learning.domain.service.ExamResultReferenceDataService;
import com.certimaster.learning.domain.service.ResultAuthorizationService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Component
class GetUserHistoryHandler {


    private final ExamResultQueryRepository examResultQueryRepository;
    private final ExamResultReferenceDataService referenceDataService;
    private final ResultAuthorizationService authorizationService;

    GetUserHistoryHandler(ExamResultQueryRepository examResultQueryRepository,
                          ExamResultReferenceDataService referenceDataService,
                          ResultAuthorizationService authorizationService) {
        this.examResultQueryRepository = examResultQueryRepository;
        this.referenceDataService = referenceDataService;
        this.authorizationService = authorizationService;
    }

    @Transactional(readOnly = true)
    UserHistoryResponse handle(GetUserHistoryQuery query) {
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

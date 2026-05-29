package com.certimaster.learning.adapter.web;

import com.certimaster.shared.security.SecurityUtils;
import com.certimaster.learning.application.query.GetQuestionReportQuery;
import com.certimaster.learning.application.query.GetResultDetailQuery;
import com.certimaster.learning.application.query.GetUserHistoryQuery;
import com.certimaster.learning.adapter.web.request.QuestionReportRequest;
import com.certimaster.learning.adapter.web.request.UserHistoryRequest;
import com.certimaster.learning.adapter.web.response.QuestionReportResponse;
import com.certimaster.learning.adapter.web.response.ResultDetailResponse;
import com.certimaster.learning.adapter.web.response.UserHistoryResponse;
import com.certimaster.learning.application.query.ExamResultQueryService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/results")
public class ExamResultController {

    private static final Logger log = LoggerFactory.getLogger(ExamResultController.class);

    private final ExamResultQueryService examResultQueryService;

    public ExamResultController(ExamResultQueryService examResultQueryService) {
        this.examResultQueryService = examResultQueryService;
    }

    @GetMapping("/{resultId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ResultDetailResponse> getResultDetail(@PathVariable Long resultId) {
        log.debug("Get result detail for resultId: {}", resultId);
        Long currentUserId = SecurityUtils.getCurrentUserId();
        return ResponseEntity.ok(examResultQueryService.getResultDetail(new GetResultDetailQuery(
                resultId,
                currentUserId
        )));
    }

    @GetMapping("/{resultId}/questions")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<QuestionReportResponse> getQuestionReport(
            @PathVariable Long resultId,
            @Valid @ModelAttribute QuestionReportRequest request) {
        log.debug("Get question report for resultId: {} with filter: {}", resultId, request.getFilter());
        Long currentUserId = SecurityUtils.getCurrentUserId();
        return ResponseEntity.ok(examResultQueryService.getQuestionReport(new GetQuestionReportQuery(
                resultId,
                currentUserId,
                request.getPage(),
                request.getSize(),
                request.getFilter(),
                request.getTopicId(),
                request.getDifficulty()
        )));
    }

    @GetMapping("/user/{userId}/history")
    @PreAuthorize("isAuthenticated() and (#userId == authentication.principal.userId or hasRole('ADMIN'))")
    public ResponseEntity<UserHistoryResponse> getUserHistory(
            @PathVariable Long userId,
            @Valid @ModelAttribute UserHistoryRequest request) {
        log.debug("Get user history for userId: {}", userId);
        Long currentUserId = SecurityUtils.getCurrentUserId();
        return ResponseEntity.ok(examResultQueryService.getUserHistory(new GetUserHistoryQuery(
                userId,
                currentUserId,
                request.getCertificationId(),
                request.getExamId(),
                request.getStatus(),
                request.getStartDate(),
                request.getEndDate(),
                request.getPage(),
                request.getSize(),
                request.getSort()
        )));
    }
}

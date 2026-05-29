package com.certimaster.learning.application.query;

import com.certimaster.shared.dto.PageResponse;
import com.certimaster.shared.exception.ResourceNotFoundException;
import com.certimaster.learning.application.query.GetQuestionReportQuery;
import com.certimaster.learning.adapter.web.response.CorrectAnswerInfo;
import com.certimaster.learning.adapter.web.response.QuestionReportItem;
import com.certimaster.learning.adapter.web.response.QuestionReportResponse;
import com.certimaster.learning.adapter.web.response.QuestionSummary;
import com.certimaster.learning.adapter.web.response.ReferenceLink;
import com.certimaster.learning.adapter.web.response.UserAnswerInfo;
import com.certimaster.learning.domain.entity.ExamResult;
import com.certimaster.learning.domain.entity.QuestionResult;
import com.certimaster.learning.adapter.persistence.ExamResultQueryRepository;
import com.certimaster.learning.adapter.persistence.QuestionResultRepository;
import com.certimaster.learning.domain.service.ResultAuthorizationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Component
class GetQuestionReportHandler {

    private static final Logger log = LoggerFactory.getLogger(GetQuestionReportHandler.class);

    private final ExamResultQueryRepository examResultQueryRepository;
    private final QuestionResultRepository questionResultRepository;
    private final ResultAuthorizationService authorizationService;

    GetQuestionReportHandler(ExamResultQueryRepository examResultQueryRepository,
                             QuestionResultRepository questionResultRepository,
                             ResultAuthorizationService authorizationService) {
        this.examResultQueryRepository = examResultQueryRepository;
        this.questionResultRepository = questionResultRepository;
        this.authorizationService = authorizationService;
    }

    @Transactional(readOnly = true)
    QuestionReportResponse handle(GetQuestionReportQuery query) {
        log.debug("Getting question report for resultId={}, currentUserId={}, filter={}",
                query.resultId(), query.currentUserId(), query.filter());

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
}

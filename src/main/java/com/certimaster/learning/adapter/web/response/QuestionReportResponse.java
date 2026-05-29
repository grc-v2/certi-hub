package com.certimaster.learning.adapter.web.response;

import com.certimaster.shared.dto.PageResponse;

import java.util.List;

/**
 * Response containing a paginated question-by-question breakdown of an exam result.
 * Includes the list of questions with answers, summary counts, and pagination info.
 */
public record QuestionReportResponse(
    Long resultId,
    List<QuestionReportItem> questions,
    QuestionSummary summary,
    PageResponse<?> pagination
) {}

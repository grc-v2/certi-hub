package com.certimaster.learning.adapter.web.response;

import com.certimaster.shared.dto.PageResponse;

import java.util.List;

/**
 * Response containing a user's exam history with statistics and pagination.
 * Includes list of exam results, aggregated statistics, and pagination info.
 */
public record UserHistoryResponse(
    Long userId,
    List<HistoryItem> results,
    HistoryStatistics statistics,
    PageResponse<?> pagination
) {}

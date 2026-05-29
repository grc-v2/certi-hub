package com.certimaster.learning.application.query;

import java.time.LocalDateTime;

public record GetUserHistoryQuery(
        Long userId,
        Long currentUserId,
        Long certificationId,
        Long examId,
        String status,
        LocalDateTime startDate,
        LocalDateTime endDate,
        Integer page,
        Integer size,
        String sort
) {
}

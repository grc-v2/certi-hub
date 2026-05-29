package com.certimaster.learning.domain.event;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record ResultCreatedEvent(
        String eventId,
        Integer eventVersion,
        String sagaId,
        Long resultId,
        Long sessionId,
        Long userId,
        Long examId,
        Long certificationId,
        Boolean passed,
        BigDecimal percentage,
        Integer timeTakenSeconds,
        Instant createdAt
) implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    public ResultCreatedEvent {
        eventId = eventId == null ? UUID.randomUUID().toString() : eventId;
        eventVersion = eventVersion == null ? 1 : eventVersion;
    }

    public ResultCreatedEvent(String sagaId,
                                  Long resultId,
                                  Long sessionId,
                                  Long userId,
                                  Long examId,
                                  Long certificationId,
                                  Boolean passed,
                                  BigDecimal percentage,
                                  Integer timeTakenSeconds,
                                  Instant createdAt) {
        this(null, null, sagaId, resultId, sessionId, userId, examId, certificationId, passed, percentage, timeTakenSeconds, createdAt);
    }
}

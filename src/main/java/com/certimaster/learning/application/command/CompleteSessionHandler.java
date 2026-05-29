package com.certimaster.learning.application.command;

import com.certimaster.learning.application.command.CompleteSessionCommand;
import com.certimaster.learning.adapter.web.response.ExamResultResponse;
import com.certimaster.learning.domain.service.ExamSessionFinalizer;
import com.certimaster.learning.domain.service.ExamSessionGuard;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
class CompleteSessionHandler {

    private static final Logger log = LoggerFactory.getLogger(CompleteSessionHandler.class);

    private final ExamSessionFinalizer finalizer;

    CompleteSessionHandler(ExamSessionFinalizer finalizer) {
        this.finalizer = finalizer;
    }

    @Transactional
    ExamResultResponse handle(CompleteSessionCommand command) {
        log.info("Completing session {} for user {}", command.sessionId(), command.userId());
        ExamResultResponse result = finalizer.finalizeUserSession(
                command.sessionId(),
                command.userId(),
                ExamSessionGuard.STATUS_COMPLETED
        );
        log.info("Session {} completed for user {} with score {}%",
                command.sessionId(), command.userId(), result.getPercentage());
        return result;
    }
}

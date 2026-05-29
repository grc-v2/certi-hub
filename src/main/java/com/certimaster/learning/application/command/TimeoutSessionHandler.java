package com.certimaster.learning.application.command;

import com.certimaster.learning.application.command.TimeoutSessionCommand;
import com.certimaster.learning.adapter.web.response.ExamResultResponse;
import com.certimaster.learning.domain.service.ExamSessionFinalizer;
import com.certimaster.learning.domain.service.ExamSessionGuard;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class TimeoutSessionHandler {

    private static final Logger log = LoggerFactory.getLogger(TimeoutSessionHandler.class);

    private final ExamSessionFinalizer finalizer;

    public TimeoutSessionHandler(ExamSessionFinalizer finalizer) {
        this.finalizer = finalizer;
    }

    @Transactional
    ExamResultResponse handle(TimeoutSessionCommand command) {
        log.info("Timing out session {} for user {}", command.sessionId(), command.userId());
        return finalizer.finalizeUserSession(
                command.sessionId(),
                command.userId(),
                ExamSessionGuard.STATUS_TIMED_OUT
        );
    }

    @Transactional
    public ExamResultResponse expire(Long sessionId) {
        return finalizer.finalizeExpiredSession(sessionId);
    }
}

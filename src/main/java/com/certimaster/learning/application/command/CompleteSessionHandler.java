package com.certimaster.learning.application.command;

import com.certimaster.learning.application.command.CompleteSessionCommand;
import com.certimaster.learning.adapter.web.response.ExamResultResponse;
import com.certimaster.learning.domain.entity.UserExamSession;
import com.certimaster.learning.domain.service.ExamSessionFinalizer;
import com.certimaster.learning.domain.service.ExamSessionGuard;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Component
class CompleteSessionHandler {


    private final ExamSessionFinalizer finalizer;
    private final ExamSessionGuard sessionGuard;

    CompleteSessionHandler(ExamSessionFinalizer finalizer, ExamSessionGuard sessionGuard) {
        this.finalizer = finalizer;
        this.sessionGuard = sessionGuard;
    }

    @Transactional
    ExamResultResponse handle(CompleteSessionCommand command) {
        UserExamSession session = sessionGuard.findSessionOrThrow(command.sessionId());
        sessionGuard.validateSessionOwner(session, command.userId());

        LocalDateTime deadline = sessionGuard.computeDeadline(session);
        String targetStatus = (deadline != null && LocalDateTime.now().isAfter(deadline))
                ? ExamSessionGuard.STATUS_TIMED_OUT
                : ExamSessionGuard.STATUS_COMPLETED;

        return finalizer.finalizeUserSession(
                command.sessionId(),
                command.userId(),
                targetStatus
        );
    }
}

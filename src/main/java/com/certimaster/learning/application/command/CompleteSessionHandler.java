package com.certimaster.learning.application.command;

import com.certimaster.learning.application.command.CompleteSessionCommand;
import com.certimaster.learning.adapter.web.response.ExamResultResponse;
import com.certimaster.learning.domain.service.ExamSessionFinalizer;
import com.certimaster.learning.domain.service.ExamSessionGuard;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
class CompleteSessionHandler {


    private final ExamSessionFinalizer finalizer;

    CompleteSessionHandler(ExamSessionFinalizer finalizer) {
        this.finalizer = finalizer;
    }

    @Transactional
    ExamResultResponse handle(CompleteSessionCommand command) {
        ExamResultResponse result = finalizer.finalizeUserSession(
                command.sessionId(),
                command.userId(),
                ExamSessionGuard.STATUS_COMPLETED
        );
        return result;
    }
}

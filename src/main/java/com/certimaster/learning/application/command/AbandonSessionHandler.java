package com.certimaster.learning.application.command;

import com.certimaster.learning.application.command.AbandonSessionCommand;
import com.certimaster.learning.domain.entity.UserExamSession;
import com.certimaster.learning.adapter.persistence.UserExamSessionRepository;
import com.certimaster.learning.domain.service.ExamSessionGuard;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
class AbandonSessionHandler {


    private final UserExamSessionRepository userExamSessionRepository;
    private final ExamSessionGuard sessionGuard;

    AbandonSessionHandler(UserExamSessionRepository userExamSessionRepository,
                          ExamSessionGuard sessionGuard) {
        this.userExamSessionRepository = userExamSessionRepository;
        this.sessionGuard = sessionGuard;
    }

    @Transactional
    void handle(AbandonSessionCommand command) {
        UserExamSession session = sessionGuard.findSessionOrThrow(command.sessionId());
        sessionGuard.validateSessionOwner(session, command.userId());

        session.abandon();
        userExamSessionRepository.save(session);
    }
}

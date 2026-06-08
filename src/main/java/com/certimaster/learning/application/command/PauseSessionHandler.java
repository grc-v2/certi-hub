package com.certimaster.learning.application.command;

import com.certimaster.learning.domain.entity.UserExamSession;
import com.certimaster.learning.adapter.persistence.UserExamSessionRepository;
import com.certimaster.learning.domain.service.ExamSessionGuard;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import static com.certimaster.learning.domain.service.ExamSessionGuard.STATUS_PAUSED;

@Component
class PauseSessionHandler {

    private final UserExamSessionRepository userExamSessionRepository;
    private final ExamSessionGuard sessionGuard;

    PauseSessionHandler(UserExamSessionRepository userExamSessionRepository,
                        ExamSessionGuard sessionGuard) {
        this.userExamSessionRepository = userExamSessionRepository;
        this.sessionGuard = sessionGuard;
    }

    @Transactional
    void handle(PauseSessionCommand command) {
        UserExamSession session = sessionGuard.findSessionOrThrow(command.sessionId());
        sessionGuard.validateSessionOwner(session, command.userId());
        sessionGuard.validateSessionPausable(session);

        if (command.timeSpentSeconds() != null && command.timeSpentSeconds() > 0) {
            session.setTimeSpentSeconds(session.getTimeSpentSeconds() + command.timeSpentSeconds());
        }
        session.setStatus(STATUS_PAUSED);
        userExamSessionRepository.save(session);
    }
}

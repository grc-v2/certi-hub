package com.certimaster.learning.application.command;

import com.certimaster.learning.application.command.AbandonSessionCommand;
import com.certimaster.learning.domain.entity.UserExamSession;
import com.certimaster.learning.adapter.persistence.UserExamSessionRepository;
import com.certimaster.learning.domain.service.ExamSessionGuard;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static com.certimaster.learning.domain.service.ExamSessionGuard.STATUS_ABANDONED;

@Component
class AbandonSessionHandler {

    private static final Logger log = LoggerFactory.getLogger(AbandonSessionHandler.class);

    private final UserExamSessionRepository userExamSessionRepository;
    private final ExamSessionGuard sessionGuard;

    AbandonSessionHandler(UserExamSessionRepository userExamSessionRepository,
                          ExamSessionGuard sessionGuard) {
        this.userExamSessionRepository = userExamSessionRepository;
        this.sessionGuard = sessionGuard;
    }

    @Transactional
    void handle(AbandonSessionCommand command) {
        log.info("Abandoning session {} for user {}", command.sessionId(), command.userId());

        UserExamSession session = sessionGuard.findSessionOrThrow(command.sessionId());
        sessionGuard.validateSessionOwner(session, command.userId());
        sessionGuard.validateSessionActive(session);

        session.setStatus(STATUS_ABANDONED);
        session.setEndTime(LocalDateTime.now());
        userExamSessionRepository.save(session);
    }
}

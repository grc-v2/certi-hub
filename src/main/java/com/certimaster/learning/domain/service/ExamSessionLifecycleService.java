package com.certimaster.learning.domain.service;

import com.certimaster.learning.domain.entity.UserExamSession;
import com.certimaster.learning.adapter.persistence.UserExamSessionRepository;
import com.certimaster.learning.application.command.TimeoutSessionHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ExamSessionLifecycleService {

    private static final Logger log = LoggerFactory.getLogger(ExamSessionLifecycleService.class);

    private final UserExamSessionRepository userExamSessionRepository;
    private final TimeoutSessionHandler timeoutSessionHandler;

    @Value("${exam.session.auto-expire-enabled:true}")
    private boolean autoExpireEnabled;

    ExamSessionLifecycleService(UserExamSessionRepository userExamSessionRepository,
                                TimeoutSessionHandler timeoutSessionHandler) {
        this.userExamSessionRepository = userExamSessionRepository;
        this.timeoutSessionHandler = timeoutSessionHandler;
    }

    /**
     * Auto-expire sessions that have exceeded their duration.
     * Runs every minute; only active when auto-expire-enabled = true.
     */
    @Scheduled(fixedDelayString = "${exam.session.auto-expire-check-interval-ms:60000}")
    public void expireTimedOutSessions() {
        if (!autoExpireEnabled) {
            return;
        }
        List<UserExamSession> expired = userExamSessionRepository.findExpiredSessions(LocalDateTime.now());
        if (expired.isEmpty()) {
            return;
        }
        log.info("Auto-expiring {} timed-out sessions", expired.size());
        for (UserExamSession session : expired) {
            try {
                timeoutSessionHandler.expire(session.getId());
                log.info("Auto-expired session {}", session.getId());
            } catch (Exception ex) {
                log.error("Failed to auto-expire session {}", session.getId(), ex);
            }
        }
    }
}

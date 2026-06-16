package com.certimaster.learning.domain.service;

import com.certimaster.learning.domain.entity.UserExamSession;
import com.certimaster.learning.adapter.persistence.UserExamSessionRepository;
import com.certimaster.learning.application.command.ExamSessionCommandService;
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
    private final ExamSessionCommandService examSessionCommandService;

    @Value("${exam.session.auto-expire-enabled:true}")
    private boolean autoExpireEnabled;

    ExamSessionLifecycleService(UserExamSessionRepository userExamSessionRepository,
                                ExamSessionCommandService examSessionCommandService) {
        this.userExamSessionRepository = userExamSessionRepository;
        this.examSessionCommandService = examSessionCommandService;
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
        for (UserExamSession session : expired) {
            try {
                examSessionCommandService.expire(session.getId());
            } catch (Exception ex) {
                log.error("Failed to auto-expire session {}", session.getId(), ex);
            }
        }
    }
}

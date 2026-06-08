package com.certimaster.learning.domain.service;

import com.certimaster.shared.exception.BusinessException;
import com.certimaster.shared.exception.ResourceNotFoundException;
import com.certimaster.learning.domain.entity.UserExamSession;
import com.certimaster.learning.adapter.persistence.UserExamSessionRepository;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class ExamSessionGuard {

    public static final String SESSION_RESOURCE = "ExamSession";
    public static final String EXAM_RESOURCE = "Exam";
    public static final String STATUS_IN_PROGRESS = "IN_PROGRESS";
    public static final String STATUS_PAUSED = "PAUSED";
    public static final String STATUS_COMPLETED = "COMPLETED";
    public static final String STATUS_TIMED_OUT = "TIMED_OUT";
    public static final String STATUS_ABANDONED = "ABANDONED";
    public static final String MODE_PRACTICE = "PRACTICE";
    public static final String MODE_EXAM = "EXAM";

    private final UserExamSessionRepository userExamSessionRepository;

    public ExamSessionGuard(UserExamSessionRepository userExamSessionRepository) {
        this.userExamSessionRepository = userExamSessionRepository;
    }

    public UserExamSession findSessionOrThrow(Long sessionId) {
        return userExamSessionRepository.findById(sessionId)
                .orElseThrow(() -> ResourceNotFoundException.byId(SESSION_RESOURCE, sessionId));
    }

    public void validateSessionOwner(UserExamSession session, Long userId) {
        if (!session.getUserId().equals(userId)) {
            throw BusinessException.invalidInput("You don't have access to this session");
        }
    }

    public void validateSessionActive(UserExamSession session) {
        if (!STATUS_IN_PROGRESS.equals(session.getStatus())) {
            throw BusinessException.invalidInput("This session is no longer active");
        }
    }

    public void validateSessionPausable(UserExamSession session) {
        if (!MODE_PRACTICE.equals(session.getMode())) {
            throw BusinessException.invalidInput("Only practice sessions can be paused");
        }
        if (!STATUS_IN_PROGRESS.equals(session.getStatus())) {
            throw BusinessException.invalidInput("Only active sessions can be paused");
        }
    }

    public void validateSessionResumable(UserExamSession session) {
        if (!STATUS_PAUSED.equals(session.getStatus())) {
            throw BusinessException.invalidInput("Only paused sessions can be resumed");
        }
    }

    public void assertWithinDeadline(UserExamSession session) {
        LocalDateTime deadline = computeDeadline(session);
        if (deadline != null && LocalDateTime.now().isAfter(deadline)) {
            throw BusinessException.invalidInput("Session deadline has passed");
        }
    }

    public LocalDateTime computeDeadline(UserExamSession session) {
        if (session.getStartTime() == null || session.getDurationMinutes() == null) {
            return null;
        }
        return session.getStartTime().plusMinutes(session.getDurationMinutes());
    }
}

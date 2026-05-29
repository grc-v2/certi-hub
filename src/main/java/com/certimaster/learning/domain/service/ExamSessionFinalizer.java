package com.certimaster.learning.domain.service;

import com.certimaster.shared.exception.BusinessException;
import com.certimaster.learning.adapter.web.response.ExamResultResponse;
import com.certimaster.learning.adapter.web.response.SessionScoreResult;
import com.certimaster.learning.domain.entity.UserAnswer;
import com.certimaster.learning.domain.entity.UserExamSession;
import com.certimaster.learning.adapter.persistence.UserAnswerRepository;
import com.certimaster.learning.adapter.persistence.UserExamSessionRepository;
import com.certimaster.learning.adapter.persistence.ExamResultWriteRepository;
import com.certimaster.learning.application.command.ExamResultProcessingService;
import com.certimaster.learning.domain.event.ExamCompletedEvent;
import com.certimaster.learning.domain.event.ResultCreatedEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

import static com.certimaster.learning.domain.service.ExamSessionGuard.STATUS_IN_PROGRESS;

@Component
public class ExamSessionFinalizer {

    private final UserExamSessionRepository userExamSessionRepository;
    private final UserAnswerRepository userAnswerRepository;
    private final ExamResultWriteRepository examResultRepository;
    private final ExamResultProcessingService examResultProcessingService;
    private final ApplicationEventPublisher applicationEvents;
    private final ExamSessionGuard sessionGuard;
    private final GradingService gradingService;
    private final ExamCompletedEventFactory examCompletedEventFactory;
    private final ResultCreatedEventFactory resultCreatedEventFactory;

    public ExamSessionFinalizer(UserExamSessionRepository userExamSessionRepository,
                                UserAnswerRepository userAnswerRepository,
                                ExamResultWriteRepository examResultRepository,
                                ExamResultProcessingService examResultProcessingService,
                                ApplicationEventPublisher applicationEvents,
                                ExamSessionGuard sessionGuard,
                                GradingService gradingService,
                                ExamCompletedEventFactory examCompletedEventFactory,
                                ResultCreatedEventFactory resultCreatedEventFactory) {
        this.userExamSessionRepository = userExamSessionRepository;
        this.userAnswerRepository = userAnswerRepository;
        this.examResultRepository = examResultRepository;
        this.examResultProcessingService = examResultProcessingService;
        this.applicationEvents = applicationEvents;
        this.sessionGuard = sessionGuard;
        this.gradingService = gradingService;
        this.examCompletedEventFactory = examCompletedEventFactory;
        this.resultCreatedEventFactory = resultCreatedEventFactory;
    }

    public ExamResultResponse finalizeUserSession(Long sessionId, Long userId, String targetStatus) {
        UserExamSession session = sessionGuard.findSessionOrThrow(sessionId);
        sessionGuard.validateSessionOwner(session, userId);
        return finalizeSession(session, targetStatus, false);
    }

    public ExamResultResponse finalizeExpiredSession(Long sessionId) {
        UserExamSession session = sessionGuard.findSessionOrThrow(sessionId);
        return finalizeSession(session, ExamSessionGuard.STATUS_TIMED_OUT, true);
    }

    private ExamResultResponse finalizeSession(UserExamSession session, String targetStatus, boolean fromScheduler) {
        if (!STATUS_IN_PROGRESS.equals(session.getStatus())) {
            if (targetStatus.equals(session.getStatus())) {
                return processResultIdempotently(session, false);
            }
            if (fromScheduler) {
                throw BusinessException.invalidInput("Session is no longer active");
            }
            throw BusinessException.invalidInput("This session is no longer active");
        }

        LocalDateTime endTime = LocalDateTime.now();
        List<UserAnswer> userAnswers = userAnswerRepository.findByUserExamSessionId(session.getId());
        SessionScoreResult scoreResult = gradingService.calculateSessionScore(session, userAnswers);

        session.setStatus(targetStatus);
        session.setEndTime(endTime);
        session.setScore(scoreResult.score());
        session.setScorePercentage(scoreResult.scorePercentage());
        session.setPassed(scoreResult.passed());
        session.setDomainScores(scoreResult.domainScores());

        userExamSessionRepository.save(session);

        return processResult(session, userAnswers, endTime, true);
    }

    private ExamResultResponse processResultIdempotently(UserExamSession session, boolean publishWhenCreated) {
        List<UserAnswer> userAnswers = userAnswerRepository.findByUserExamSessionId(session.getId());
        LocalDateTime endTime = session.getEndTime() != null ? session.getEndTime() : LocalDateTime.now();
        return processResult(session, userAnswers, endTime, publishWhenCreated);
    }

    private ExamResultResponse processResult(UserExamSession session,
                                             List<UserAnswer> userAnswers,
                                             LocalDateTime endTime,
                                             boolean publishWhenCreated) {
        boolean resultAlreadyExists = examResultRepository.findBySessionId(session.getId()).isPresent();
        ExamCompletedEvent completedEvent = examCompletedEventFactory.create(session, userAnswers, endTime);
        ExamResultResponse result = examResultProcessingService.processCompletedExam(completedEvent);

        if (publishWhenCreated && !resultAlreadyExists) {
            ResultCreatedEvent resultCreatedEvent = resultCreatedEventFactory.create(session, result);
            applicationEvents.publishEvent(completedEvent);
            applicationEvents.publishEvent(resultCreatedEvent);
        }

        return result;
    }
}

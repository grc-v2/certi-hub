package com.certimaster.learning.application.command;

import com.certimaster.shared.exception.BusinessException;
import com.certimaster.learning.application.command.SubmitAnswerCommand;
import com.certimaster.learning.adapter.web.response.AnswerFeedbackResponse;
import com.certimaster.learning.adapter.web.response.GradingResult;
import com.certimaster.learning.domain.entity.UserAnswer;
import com.certimaster.learning.domain.entity.UserExamSession;
import com.certimaster.learning.adapter.persistence.UserAnswerRepository;
import com.certimaster.learning.adapter.persistence.UserExamSessionRepository;
import com.certimaster.learning.domain.service.ExamSessionGuard;
import com.certimaster.learning.domain.service.GradingService;
import com.certimaster.learning.domain.entity.Question;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

import static com.certimaster.learning.domain.service.ExamSessionGuard.MODE_PRACTICE;

@Component
class SubmitAnswerHandler {

    private static final Logger log = LoggerFactory.getLogger(SubmitAnswerHandler.class);

    private final UserAnswerRepository userAnswerRepository;
    private final UserExamSessionRepository userExamSessionRepository;
    private final GradingService gradingService;
    private final ExamSessionGuard sessionGuard;

    SubmitAnswerHandler(UserAnswerRepository userAnswerRepository,
                        UserExamSessionRepository userExamSessionRepository,
                        GradingService gradingService,
                        ExamSessionGuard sessionGuard) {
        this.userAnswerRepository = userAnswerRepository;
        this.userExamSessionRepository = userExamSessionRepository;
        this.gradingService = gradingService;
        this.sessionGuard = sessionGuard;
    }

    @Transactional
    AnswerFeedbackResponse handle(SubmitAnswerCommand command) {
        log.debug("Submitting answer for session {} question {}", command.sessionId(), command.questionId());

        UserExamSession session = sessionGuard.findSessionOrThrow(command.sessionId());
        sessionGuard.validateSessionOwner(session, command.userId());
        sessionGuard.validateSessionActive(session);

        UserAnswer userAnswer = userAnswerRepository.findByUserExamSessionIdAndQuestion_Id(command.sessionId(), command.questionId())
                .orElseThrow(() -> BusinessException.invalidInput(
                        String.format("Question %d is not part of this session", command.questionId())));

        Question question = userAnswer.getQuestion();

        GradingResult gradingResult = gradingService.gradeAnswer(question, command.selectedOptionIds());
        boolean isCorrect = gradingResult.correct();

        boolean wasAnsweredBefore = userAnswer.getSelectedOptionIds() != null;
        Boolean wasPreviouslyCorrect = userAnswer.getIsCorrect();

        userAnswer.setSelectedOptionIds(command.selectedOptionIds().toArray(new Long[0]));
        userAnswer.setCorrectOptionIds(gradingResult.correctOptionIds().toArray(new Long[0]));
        userAnswer.setIsCorrect(isCorrect);
        userAnswer.setAnsweredAt(LocalDateTime.now());

        if (command.timeSpentSeconds() != null) {
            userAnswer.setTimeSpentSeconds(userAnswer.getTimeSpentSeconds() + command.timeSpentSeconds());
        }

        if (command.isFlagged() != null) {
            boolean wasFlagged = Boolean.TRUE.equals(userAnswer.getIsFlagged());
            boolean nowFlagged = command.isFlagged();
            if (wasFlagged != nowFlagged) {
                userAnswer.setIsFlagged(nowFlagged);
                if (nowFlagged) {
                    session.setFlaggedCount(session.getFlaggedCount() + 1);
                } else {
                    session.setFlaggedCount(Math.max(0, session.getFlaggedCount() - 1));
                }
            }
        }

        userAnswerRepository.save(userAnswer);
        updateSessionStatistics(session, wasAnsweredBefore, wasPreviouslyCorrect, isCorrect);

        if (command.timeSpentSeconds() != null) {
            session.setTimeSpentSeconds(session.getTimeSpentSeconds() + command.timeSpentSeconds());
        }

        userExamSessionRepository.save(session);

        AnswerFeedbackResponse.Builder responseBuilder = AnswerFeedbackResponse.builder()
                .questionId(question.getId())
                .answered(true);

        if (MODE_PRACTICE.equals(session.getMode())) {
            List<String> referenceLinks = question.getReferenceUrl() != null
                    ? List.of(question.getReferenceUrl())
                    : null;
            responseBuilder
                    .isCorrect(isCorrect)
                    .correctOptionIds(gradingResult.correctOptionIds())
                    .explanation(question.getExplanation())
                    .referenceLinks(referenceLinks);
        }

        return responseBuilder.build();
    }

    private void updateSessionStatistics(UserExamSession session,
                                         boolean wasAnsweredBefore,
                                         Boolean wasPreviouslyCorrect,
                                         boolean isCorrect) {
        if (!wasAnsweredBefore) {
            session.setAnsweredCount(session.getAnsweredCount() + 1);
            session.setUnansweredCount(Math.max(0, session.getUnansweredCount() - 1));
            if (isCorrect) {
                session.setCorrectCount(session.getCorrectCount() + 1);
            } else {
                session.setWrongCount(session.getWrongCount() + 1);
            }
        } else if (wasPreviouslyCorrect != null && wasPreviouslyCorrect != isCorrect) {
            if (isCorrect) {
                session.setCorrectCount(session.getCorrectCount() + 1);
                session.setWrongCount(Math.max(0, session.getWrongCount() - 1));
            } else {
                session.setCorrectCount(Math.max(0, session.getCorrectCount() - 1));
                session.setWrongCount(session.getWrongCount() + 1);
            }
        }
    }
}

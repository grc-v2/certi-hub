package com.certimaster.learning.application.command;

import com.certimaster.shared.exception.BusinessException;
import com.certimaster.learning.application.command.SubmitAnswerCommand;
import com.certimaster.learning.adapter.web.response.AnswerFeedbackResponse;
import com.certimaster.learning.domain.model.Grade;
import com.certimaster.learning.domain.entity.UserAnswer;
import com.certimaster.learning.domain.entity.UserExamSession;
import com.certimaster.learning.adapter.persistence.UserAnswerRepository;
import com.certimaster.learning.adapter.persistence.UserExamSessionRepository;
import com.certimaster.learning.domain.service.ExamSessionGuard;
import com.certimaster.learning.domain.entity.Question;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.certimaster.learning.domain.service.ExamSessionGuard.MODE_PRACTICE;

@Component
class SubmitAnswerHandler {


    private final UserAnswerRepository userAnswerRepository;
    private final UserExamSessionRepository userExamSessionRepository;
    private final ExamSessionGuard sessionGuard;

    SubmitAnswerHandler(UserAnswerRepository userAnswerRepository,
                        UserExamSessionRepository userExamSessionRepository,
                        ExamSessionGuard sessionGuard) {
        this.userAnswerRepository = userAnswerRepository;
        this.userExamSessionRepository = userExamSessionRepository;
        this.sessionGuard = sessionGuard;
    }

    @Transactional
    AnswerFeedbackResponse handle(SubmitAnswerCommand command) {
        UserExamSession session = sessionGuard.findSessionOrThrow(command.sessionId());
        sessionGuard.validateSessionOwner(session, command.userId());
        session.ensureActive();
        session.ensureWithinDeadline();

        UserAnswer userAnswer = userAnswerRepository.findByUserExamSessionIdAndQuestion_Id(command.sessionId(), command.questionId())
                .orElseThrow(() -> BusinessException.invalidInput(
                        String.format("Question %d is not part of this session", command.questionId())));

        Question question = userAnswer.getQuestion();

        Grade grade = session.recordAnswer(
                userAnswer,
                command.selectedOptionIds(),
                command.timeSpentSeconds(),
                command.isFlagged());

        userAnswerRepository.save(userAnswer);
        userExamSessionRepository.save(session);

        AnswerFeedbackResponse.Builder responseBuilder = AnswerFeedbackResponse.builder()
                .questionId(question.getId())
                .answered(true);

        if (MODE_PRACTICE.equals(session.getMode())) {
            List<String> referenceLinks = question.getReferenceUrl() != null
                    ? List.of(question.getReferenceUrl())
                    : null;
            responseBuilder
                    .isCorrect(grade.correct())
                    .correctOptionIds(grade.correctOptionIds())
                    .explanation(question.getExplanation())
                    .referenceLinks(referenceLinks);
        }

        return responseBuilder.build();
    }
}

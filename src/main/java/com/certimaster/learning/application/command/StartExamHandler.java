package com.certimaster.learning.application.command;

import com.certimaster.shared.exception.BusinessException;
import com.certimaster.shared.exception.ResourceNotFoundException;
import com.certimaster.learning.application.command.StartExamCommand;
import com.certimaster.learning.adapter.web.response.ExamSessionResponse;
import com.certimaster.learning.domain.entity.Exam;
import com.certimaster.learning.domain.entity.UserAnswer;
import com.certimaster.learning.domain.entity.UserExamSession;
import com.certimaster.learning.adapter.persistence.ExamWriteRepository;
import com.certimaster.learning.adapter.persistence.UserExamSessionRepository;
import com.certimaster.learning.domain.service.ExamSessionQuestionInitializer;
import com.certimaster.learning.domain.service.QuestionSelectionService;
import com.certimaster.learning.adapter.web.response.QuestionResponse;
import com.certimaster.learning.domain.entity.Question;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

import static com.certimaster.learning.domain.service.ExamSessionGuard.EXAM_RESOURCE;
import static com.certimaster.learning.domain.service.ExamSessionGuard.STATUS_IN_PROGRESS;

@Component
class StartExamHandler {


    private final ExamWriteRepository examRepository;
    private final UserExamSessionRepository userExamSessionRepository;
    private final QuestionSelectionService questionSelectionService;
    private final ExamSessionQuestionInitializer questionInitializer;

    @Value("${exam.session.max-concurrent:3}")
    private int maxConcurrentSessions;

    StartExamHandler(ExamWriteRepository examRepository,
                     UserExamSessionRepository userExamSessionRepository,
                     QuestionSelectionService questionSelectionService,
                     ExamSessionQuestionInitializer questionInitializer) {
        this.examRepository = examRepository;
        this.userExamSessionRepository = userExamSessionRepository;
        this.questionSelectionService = questionSelectionService;
        this.questionInitializer = questionInitializer;
    }

    @Transactional
    ExamSessionResponse handle(StartExamCommand command) {
        validateConcurrentSessions(command.userId());

        userExamSessionRepository.findByUserIdAndExam_IdAndStatus(command.userId(), command.examId(), STATUS_IN_PROGRESS)
                .ifPresent(existingSession -> {
                    throw BusinessException.invalidInput(
                            String.format("You already have an active session (ID: %d) for this exam. Please complete or abandon it first.",
                                    existingSession.getId()));
                });

        Exam exam = examRepository.findActiveById(command.examId())
                .orElseThrow(() -> ResourceNotFoundException.byId(EXAM_RESOURCE, command.examId()));

        List<Question> questions = questionSelectionService.selectQuestionsForExam(exam);
        if (questions.isEmpty()) {
            throw BusinessException.invalidInput("This exam has no questions");
        }

        LocalDateTime startTime = LocalDateTime.now();

        UserExamSession session = UserExamSession.builder()
                .userId(command.userId())
                .exam(exam)
                .certification(exam.getCertification())
                .startTime(startTime)
                .status(STATUS_IN_PROGRESS)
                .mode(command.mode())
                .examTitle(exam.getTitle())
                .totalQuestions(questions.size())
                .durationMinutes(exam.getDurationMinutes())
                .answeredCount(0)
                .correctCount(0)
                .wrongCount(0)
                .unansweredCount(questions.size())
                .flaggedCount(0)
                .timeSpentSeconds(0)
                .build();

        userExamSessionRepository.save(session);
        List<UserAnswer> savedAnswers = questionInitializer.createUserAnswers(session, questions);
        List<QuestionResponse> questionResponses = questionInitializer.buildOrderedQuestionResponses(questions, savedAnswers);

        return ExamSessionResponse.builder()
                .id(session.getId())
                .examId(exam.getId())
                .examTitle(exam.getTitle())
                .certificationId(exam.getCertification().getId())
                .certificationName(exam.getCertification().getName())
                .mode(command.mode())
                .status(STATUS_IN_PROGRESS)
                .startTime(startTime)
                .durationMinutes(exam.getDurationMinutes())
                .passingScore(exam.getPassingScore())
                .totalQuestions(questions.size())
                .currentQuestionIndex(0)
                .questions(questionResponses)
                .build();
    }

    private void validateConcurrentSessions(Long userId) {
        int activeCount = userExamSessionRepository.countByUserIdAndStatus(userId, STATUS_IN_PROGRESS);
        if (activeCount >= maxConcurrentSessions) {
            throw BusinessException.invalidInput(
                    String.format("Maximum %d concurrent sessions allowed. Please complete or abandon existing sessions first.",
                            maxConcurrentSessions));
        }
    }
}

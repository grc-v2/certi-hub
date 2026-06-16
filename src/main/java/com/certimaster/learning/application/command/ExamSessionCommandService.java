package com.certimaster.learning.application.command;

import com.certimaster.shared.exception.BusinessException;
import com.certimaster.shared.exception.ResourceNotFoundException;
import com.certimaster.learning.adapter.persistence.CertificationWriteRepository;
import com.certimaster.learning.adapter.persistence.ExamWriteRepository;
import com.certimaster.learning.adapter.persistence.UserAnswerRepository;
import com.certimaster.learning.adapter.persistence.UserExamSessionRepository;
import com.certimaster.learning.adapter.web.mapper.OrderedQuestionResponseAssembler;
import com.certimaster.learning.adapter.web.response.AnswerFeedbackResponse;
import com.certimaster.learning.adapter.web.response.ExamResultResponse;
import com.certimaster.learning.adapter.web.response.ExamSessionResponse;
import com.certimaster.learning.adapter.web.response.QuestionResponse;
import com.certimaster.learning.domain.entity.Certification;
import com.certimaster.learning.domain.entity.Exam;
import com.certimaster.learning.domain.entity.Question;
import com.certimaster.learning.domain.entity.UserAnswer;
import com.certimaster.learning.domain.entity.UserExamSession;
import com.certimaster.learning.domain.model.Grade;
import com.certimaster.learning.domain.model.PracticeQuestionCriteria;
import com.certimaster.learning.domain.service.ExamSessionGuard;
import com.certimaster.learning.domain.service.ExamSessionQuestionInitializer;
import com.certimaster.learning.domain.service.QuestionSelectionService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

import static com.certimaster.learning.domain.service.ExamSessionGuard.EXAM_RESOURCE;
import static com.certimaster.learning.domain.service.ExamSessionGuard.MODE_EXAM;
import static com.certimaster.learning.domain.service.ExamSessionGuard.MODE_PRACTICE;
import static com.certimaster.learning.domain.service.ExamSessionGuard.STATUS_IN_PROGRESS;

/**
 * The single command module for the {@code UserExamSession} aggregate's lifecycle:
 * start (exam / practice), record an answer, pause, resume, abandon, complete, time out.
 *
 * <p>Each method loads the aggregate, guards access, drives one aggregate transition (or
 * delegates finalization), and persists. The aggregate owns the state machine and the
 * counters; this module owns the load/guard/save orchestration and the question-selection
 * and response-assembly that starting a session requires. Result finalization (scoring +
 * persistence) lives behind {@link ExamSessionFinalizer}.
 */
@Service
public class ExamSessionCommandService {

    private final ExamWriteRepository examRepository;
    private final CertificationWriteRepository certificationRepository;
    private final UserExamSessionRepository userExamSessionRepository;
    private final UserAnswerRepository userAnswerRepository;
    private final QuestionSelectionService questionSelectionService;
    private final ExamSessionQuestionInitializer questionInitializer;
    private final OrderedQuestionResponseAssembler questionResponseAssembler;
    private final ExamSessionGuard sessionGuard;
    private final ExamSessionFinalizer finalizer;

    @Value("${exam.session.max-concurrent:3}")
    private int maxConcurrentSessions;

    public ExamSessionCommandService(ExamWriteRepository examRepository,
                                     CertificationWriteRepository certificationRepository,
                                     UserExamSessionRepository userExamSessionRepository,
                                     UserAnswerRepository userAnswerRepository,
                                     QuestionSelectionService questionSelectionService,
                                     ExamSessionQuestionInitializer questionInitializer,
                                     OrderedQuestionResponseAssembler questionResponseAssembler,
                                     ExamSessionGuard sessionGuard,
                                     ExamSessionFinalizer finalizer) {
        this.examRepository = examRepository;
        this.certificationRepository = certificationRepository;
        this.userExamSessionRepository = userExamSessionRepository;
        this.userAnswerRepository = userAnswerRepository;
        this.questionSelectionService = questionSelectionService;
        this.questionInitializer = questionInitializer;
        this.questionResponseAssembler = questionResponseAssembler;
        this.sessionGuard = sessionGuard;
        this.finalizer = finalizer;
    }

    @Transactional
    public ExamSessionResponse startExam(StartExamCommand command) {
        validateConcurrentSessions(command.userId());

        userExamSessionRepository.findByUserIdAndExam_IdAndStatus(command.userId(), command.examId(), STATUS_IN_PROGRESS)
                .ifPresent(existingSession -> {
                    throw BusinessException.invalidInput(
                            String.format("You already have an active session (ID: %d) for this exam. Please complete or abandon it first.",
                                    existingSession.getId()));
                });

        Exam exam = examRepository.findActiveById(command.examId())
                .orElseThrow(() -> ResourceNotFoundException.byId(EXAM_RESOURCE, command.examId()));

        // Server is the source of truth: starting via this endpoint is always an EXAM session.
        // Client-supplied mode is ignored to prevent leaking feedback during a real exam.
        List<Question> questions = questionSelectionService.selectQuestionsForExam(exam, true);
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
                .mode(MODE_EXAM)
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
        List<QuestionResponse> questionResponses = questionResponseAssembler.buildOrderedQuestionResponses(questions, savedAnswers);

        LocalDateTime deadline = exam.getDurationMinutes() != null
                ? startTime.plusMinutes(exam.getDurationMinutes())
                : null;

        return ExamSessionResponse.builder()
                .id(session.getId())
                .examId(exam.getId())
                .examTitle(exam.getTitle())
                .certificationId(exam.getCertification().getId())
                .certificationName(exam.getCertification().getName())
                .mode(MODE_EXAM)
                .status(STATUS_IN_PROGRESS)
                .startTime(startTime)
                .deadline(deadline)
                .durationMinutes(exam.getDurationMinutes())
                .passingScore(exam.getPassingScore())
                .totalQuestions(questions.size())
                .currentQuestionIndex(0)
                .questions(questionResponses)
                .build();
    }

    @Transactional
    public ExamSessionResponse startPracticeSession(StartPracticeSessionCommand command) {
        validateConcurrentSessions(command.userId());

        Certification certification = certificationRepository.findById(command.certificationId())
                .orElseThrow(() -> ResourceNotFoundException.byId("Certification", command.certificationId()));

        PracticeQuestionCriteria criteria = new PracticeQuestionCriteria(
                command.certificationId(),
                command.questionBankId(),
                command.domainIds(),
                command.difficulties(),
                command.questionCount(),
                command.userId()
        );

        List<Question> questions = questionSelectionService.selectPracticeQuestions(criteria);
        if (questions.isEmpty()) {
            throw BusinessException.invalidInput("No questions found matching the criteria");
        }

        LocalDateTime startTime = LocalDateTime.now();

        UserExamSession session = UserExamSession.builder()
                .userId(command.userId())
                .certification(certification)
                .startTime(startTime)
                .status(STATUS_IN_PROGRESS)
                .mode(MODE_PRACTICE)
                .examTitle("Practice: " + certification.getName())
                .totalQuestions(questions.size())
                .answeredCount(0)
                .correctCount(0)
                .wrongCount(0)
                .unansweredCount(questions.size())
                .flaggedCount(0)
                .timeSpentSeconds(0)
                .domainFilter(command.domainIds())
                .difficultyFilter(command.difficulties())
                .build();

        userExamSessionRepository.save(session);

        List<UserAnswer> savedAnswers = questionInitializer.createUserAnswers(session, questions);
        List<QuestionResponse> questionResponses = questionResponseAssembler.buildOrderedQuestionResponses(questions, savedAnswers);

        return ExamSessionResponse.builder()
                .id(session.getId())
                .certificationId(certification.getId())
                .certificationName(certification.getName())
                .mode(MODE_PRACTICE)
                .status(STATUS_IN_PROGRESS)
                .startTime(startTime)
                .totalQuestions(questions.size())
                .currentQuestionIndex(0)
                .questions(questionResponses)
                .build();
    }

    @Transactional
    public AnswerFeedbackResponse submitAnswer(SubmitAnswerCommand command) {
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

    @Transactional
    public ExamResultResponse completeSession(CompleteSessionCommand command) {
        UserExamSession session = sessionGuard.findSessionOrThrow(command.sessionId());
        sessionGuard.validateSessionOwner(session, command.userId());

        String targetStatus = session.isPastDeadline()
                ? ExamSessionGuard.STATUS_TIMED_OUT
                : ExamSessionGuard.STATUS_COMPLETED;

        return finalizer.finalizeUserSession(command.sessionId(), command.userId(), targetStatus);
    }

    @Transactional
    public ExamResultResponse timeoutSession(TimeoutSessionCommand command) {
        return finalizer.finalizeUserSession(
                command.sessionId(),
                command.userId(),
                ExamSessionGuard.STATUS_TIMED_OUT);
    }

    /**
     * Finalize a session the scheduler found past its deadline. No owner check — the caller
     * is the system, not the session's user. See {@code ExamSessionLifecycleService}.
     */
    @Transactional
    public ExamResultResponse expire(Long sessionId) {
        return finalizer.finalizeExpiredSession(sessionId);
    }

    @Transactional
    public void abandonSession(AbandonSessionCommand command) {
        UserExamSession session = sessionGuard.findSessionOrThrow(command.sessionId());
        sessionGuard.validateSessionOwner(session, command.userId());

        session.abandon();
        userExamSessionRepository.save(session);
    }

    @Transactional
    public void pauseSession(PauseSessionCommand command) {
        UserExamSession session = sessionGuard.findSessionOrThrow(command.sessionId());
        sessionGuard.validateSessionOwner(session, command.userId());

        session.pause(command.timeSpentSeconds());
        userExamSessionRepository.save(session);
    }

    @Transactional
    public void resumePlay(ResumePlayCommand command) {
        UserExamSession session = sessionGuard.findSessionOrThrow(command.sessionId());
        sessionGuard.validateSessionOwner(session, command.userId());

        session.resume();
        userExamSessionRepository.save(session);
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

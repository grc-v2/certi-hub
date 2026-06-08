package com.certimaster.learning.application.query;

import com.certimaster.shared.exception.BusinessException;
import com.certimaster.shared.exception.ResourceNotFoundException;
import com.certimaster.learning.adapter.web.mapper.QuestionMapper;
import com.certimaster.learning.adapter.web.response.QuestionResponse;
import com.certimaster.learning.adapter.web.response.QuestionWithAnswerResponse;
import com.certimaster.learning.adapter.web.response.ResumeSessionResponse;
import com.certimaster.learning.adapter.web.response.ReviewQuestionResponse;
import com.certimaster.learning.adapter.web.response.ReviewSessionResponse;
import com.certimaster.learning.adapter.web.response.UserExamSessionResponse;
import com.certimaster.learning.domain.entity.ExamQuestion;
import com.certimaster.learning.domain.entity.Question;
import com.certimaster.learning.domain.entity.UserAnswer;
import com.certimaster.learning.domain.entity.UserExamSession;
import com.certimaster.learning.adapter.persistence.UserAnswerRepository;
import com.certimaster.learning.adapter.persistence.UserExamSessionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.certimaster.learning.adapter.web.response.QuestionOptionResponse;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.certimaster.learning.domain.service.ExamSessionGuard.MODE_PRACTICE;
import static com.certimaster.learning.domain.service.ExamSessionGuard.SESSION_RESOURCE;
import static com.certimaster.learning.domain.service.ExamSessionGuard.STATUS_IN_PROGRESS;
import static com.certimaster.learning.domain.service.ExamSessionGuard.STATUS_PAUSED;

@Service
@Transactional(readOnly = true)
public class ExamSessionQueryService {


    private final UserExamSessionRepository userExamSessionRepository;
    private final UserAnswerRepository userAnswerRepository;
    private final QuestionMapper questionMapper;

    ExamSessionQueryService(UserExamSessionRepository userExamSessionRepository,
                            UserAnswerRepository userAnswerRepository,
                            QuestionMapper questionMapper) {
        this.userExamSessionRepository = userExamSessionRepository;
        this.userAnswerRepository = userAnswerRepository;
        this.questionMapper = questionMapper;
    }

    public UserExamSessionResponse getSession(Long sessionId, Long userId) {
        UserExamSession session = userExamSessionRepository.findByIdAndUserId(sessionId, userId)
                .orElseThrow(() -> ResourceNotFoundException.byId(SESSION_RESOURCE, sessionId));
        return mapToUserExamSessionResponse(session);
    }

    public ResumeSessionResponse resumeSession(Long sessionId, Long userId) {
        UserExamSession session = userExamSessionRepository.findByIdAndUserId(sessionId, userId)
                .orElseThrow(() -> ResourceNotFoundException.byId(SESSION_RESOURCE, sessionId));

        List<UserAnswer> userAnswers = userAnswerRepository.findByUserExamSessionId(sessionId);
        Map<Long, UserAnswer> answersByQuestionId = userAnswers.stream()
                .collect(Collectors.toMap(ua -> ua.getQuestion().getId(), Function.identity()));

        List<QuestionWithAnswerResponse> questionsWithAnswers;

        if (session.getExam() != null) {
            questionsWithAnswers = session.getExam().getExamQuestions().stream()
                    .sorted(Comparator.comparing(ExamQuestion::getOrderIndex))
                    .map(eq -> buildQuestionWithAnswer(eq.getQuestion(), answersByQuestionId, session.getMode()))
                    .toList();
        } else {
            questionsWithAnswers = userAnswers.stream()
                    .sorted(Comparator.comparing(ua -> ua.getQuestionOrder() != null ? ua.getQuestionOrder() : 0))
                    .map(ua -> buildQuestionWithAnswer(ua.getQuestion(), answersByQuestionId, session.getMode()))
                    .toList();
        }

        return ResumeSessionResponse.builder()
                .id(session.getId())
                .examId(session.getExam() != null ? session.getExam().getId() : null)
                .examTitle(session.getExamTitle())
                .certificationId(session.getCertification() != null ? session.getCertification().getId() : null)
                .certificationName(session.getCertification() != null ? session.getCertification().getName() : null)
                .mode(session.getMode())
                .status(session.getStatus())
                .startTime(session.getStartTime())
                .deadline(computeDeadline(session))
                .durationMinutes(session.getDurationMinutes())
                .totalQuestions(session.getTotalQuestions())
                .answeredCount(session.getAnsweredCount())
                .unansweredCount(session.getUnansweredCount())
                .flaggedCount(session.getFlaggedCount())
                .timeSpentSeconds(session.getTimeSpentSeconds())
                .questions(questionsWithAnswers)
                .build();
    }

    public List<UserExamSessionResponse> getActiveSessions(Long userId) {
        List<UserExamSession> sessions = userExamSessionRepository.findByUserIdAndStatusIn(
                userId, List.of(STATUS_IN_PROGRESS, STATUS_PAUSED));
        return sessions.stream()
                .map(this::mapToUserExamSessionResponse)
                .toList();
    }

    public ReviewSessionResponse reviewSession(Long sessionId, Long userId) {
        UserExamSession session = userExamSessionRepository.findByIdAndUserId(sessionId, userId)
                .orElseThrow(() -> ResourceNotFoundException.byId(SESSION_RESOURCE, sessionId));

        if (STATUS_IN_PROGRESS.equals(session.getStatus()) || STATUS_PAUSED.equals(session.getStatus())) {
            throw BusinessException.invalidInput("Cannot review an active session. Please complete or abandon it first.");
        }

        List<UserAnswer> userAnswers = userAnswerRepository.findByUserExamSessionId(sessionId);

        List<ReviewQuestionResponse> reviewQuestions = userAnswers.stream()
                .sorted(Comparator.comparing(ua -> ua.getQuestionOrder() != null ? ua.getQuestionOrder() : 0))
                .map(ua -> {
                    Question question = ua.getQuestion();
                    QuestionResponse qr = questionMapper.toResponse(question);

                    List<Long> selectedIds = ua.getSelectedOptionIds() != null
                            ? Arrays.asList(ua.getSelectedOptionIds()) : Collections.emptyList();
                    List<Long> correctIds = ua.getCorrectOptionIds() != null
                            ? Arrays.asList(ua.getCorrectOptionIds()) : Collections.emptyList();

                    List<String> referenceLinks = question.getReferenceUrl() != null
                            ? List.of(question.getReferenceUrl()) : null;

                    return ReviewQuestionResponse.builder()
                            .question(qr)
                            .selectedOptionIds(selectedIds)
                            .correctOptionIds(correctIds)
                            .isCorrect(ua.getIsCorrect())
                            .explanation(question.getExplanation())
                            .referenceLinks(referenceLinks)
                            .build();
                })
                .toList();

        return ReviewSessionResponse.builder()
                .id(session.getId())
                .examId(session.getExam() != null ? session.getExam().getId() : null)
                .examTitle(session.getExamTitle())
                .certificationId(session.getCertification() != null ? session.getCertification().getId() : null)
                .certificationName(session.getCertification() != null ? session.getCertification().getName() : null)
                .mode(session.getMode())
                .status(session.getStatus())
                .startTime(session.getStartTime())
                .endTime(session.getEndTime())
                .totalQuestions(session.getTotalQuestions())
                .correctCount(session.getCorrectCount())
                .wrongCount(session.getWrongCount())
                .scorePercentage(session.getScorePercentage())
                .passed(session.getPassed())
                .timeSpentSeconds(session.getTimeSpentSeconds())
                .questions(reviewQuestions)
                .build();
    }

    private QuestionWithAnswerResponse buildQuestionWithAnswer(Question question,
                                                               Map<Long, UserAnswer> answersByQuestionId,
                                                               String mode) {
        UserAnswer ua = answersByQuestionId.get(question.getId());

        QuestionResponse qr = questionMapper.toResponseWithoutCorrect(question);

        // Apply stable option ordering from the shuffled order saved at session start
        if (ua != null && ua.getOptionOrderMap() != null && !ua.getOptionOrderMap().isEmpty()) {
            Map<Long, Integer> orderMap = ua.getOptionOrderMap();
            List<QuestionOptionResponse> sorted = qr.getOptions().stream()
                    .sorted(Comparator.comparingInt(opt -> orderMap.getOrDefault(opt.getId(), Integer.MAX_VALUE)))
                    .toList();
            qr.setOptions(sorted);
        }

        List<Long> selectedOptionIds = ua != null && ua.getSelectedOptionIds() != null
                ? Arrays.asList(ua.getSelectedOptionIds()) : null;
        Boolean isFlagged = ua != null ? ua.getIsFlagged() : false;
        Boolean isCorrect = MODE_PRACTICE.equals(mode) && ua != null ? ua.getIsCorrect() : null;

        return QuestionWithAnswerResponse.builder()
                .question(qr)
                .selectedOptionIds(selectedOptionIds)
                .isFlagged(isFlagged)
                .isCorrect(isCorrect)
                .build();
    }

    private UserExamSessionResponse mapToUserExamSessionResponse(UserExamSession session) {
        return UserExamSessionResponse.builder()
                .id(session.getId())
                .userId(session.getUserId())
                .examId(session.getExam() != null ? session.getExam().getId() : null)
                .certificationId(session.getCertification() != null ? session.getCertification().getId() : null)
                .certificationName(session.getCertification() != null ? session.getCertification().getName() : null)
                .startTime(session.getStartTime())
                .endTime(session.getEndTime())
                .deadline(computeDeadline(session))
                .status(session.getStatus())
                .mode(session.getMode())
                .examTitle(session.getExamTitle())
                .totalQuestions(session.getTotalQuestions())
                .durationMinutes(session.getDurationMinutes())
                .answeredCount(session.getAnsweredCount())
                .correctCount(session.getCorrectCount())
                .wrongCount(session.getWrongCount())
                .unansweredCount(session.getUnansweredCount())
                .flaggedCount(session.getFlaggedCount())
                .timeSpentSeconds(session.getTimeSpentSeconds())
                .createdAt(session.getCreatedAt())
                .updatedAt(session.getUpdatedAt())
                .build();
    }

    private LocalDateTime computeDeadline(UserExamSession session) {
        if (session.getStartTime() == null || session.getDurationMinutes() == null) {
            return null;
        }
        return session.getStartTime().plusMinutes(session.getDurationMinutes());
    }
}

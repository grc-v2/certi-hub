package com.certimaster.learning.application.command;

import com.certimaster.learning.adapter.web.response.ExamResultResponse;
import com.certimaster.learning.domain.entity.Exam;
import com.certimaster.learning.domain.entity.ExamResult;
import com.certimaster.learning.domain.entity.Question;
import com.certimaster.learning.domain.entity.QuestionResult;
import com.certimaster.learning.domain.entity.TopicPerformance;
import com.certimaster.learning.domain.entity.UserAnswer;
import com.certimaster.learning.domain.entity.UserExamSession;
import com.certimaster.learning.adapter.persistence.ExamResultWriteRepository;
import com.certimaster.learning.adapter.persistence.QuestionResultRepository;
import com.certimaster.learning.adapter.persistence.TopicPerformanceRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Internal collaborator of {@link ExamSessionFinalizer}: turns a finished
 * {@code UserExamSession} and its answers into the persisted {@code ExamResult},
 * {@code TopicPerformance} and {@code QuestionResult} records, and the result response.
 *
 * <p>Reads domain entities directly — it is deliberately decoupled from the externalized
 * {@code ExamCompletedEvent}, which is a wire shape built only at the publication seam.
 * Idempotent: a session that already has a result returns the stored one.
 */
@Service
public class ExamResultProcessingService {

    private static final Logger log = LoggerFactory.getLogger(ExamResultProcessingService.class);

    private final ExamResultWriteRepository examResultRepository;
    private final TopicPerformanceRepository topicPerformanceRepository;
    private final QuestionResultRepository questionResultRepository;

    public ExamResultProcessingService(ExamResultWriteRepository examResultRepository,
                                       TopicPerformanceRepository topicPerformanceRepository,
                                       QuestionResultRepository questionResultRepository) {
        this.examResultRepository = examResultRepository;
        this.topicPerformanceRepository = topicPerformanceRepository;
        this.questionResultRepository = questionResultRepository;
    }

    @Transactional
    public ExamResultResponse processCompletedExam(UserExamSession session,
                                                   List<UserAnswer> answers,
                                                   LocalDateTime endTime) {
        Long sessionId = session.getId();
        try {
            var existingResult = examResultRepository.findBySessionId(sessionId);
            if (existingResult.isPresent()) {
                log.warn("Result already exists for session {}, returning existing result", sessionId);
                return buildResponseFromExistingResult(existingResult.get());
            }

            Exam exam = session.getExam();
            int totalQuestions = session.getTotalQuestions() != null ? session.getTotalQuestions() : 0;
            int correctCount = session.getCorrectCount() != null ? session.getCorrectCount() : 0;
            int wrongCount = session.getWrongCount() != null ? session.getWrongCount() : 0;
            int unanswered = totalQuestions - (correctCount + wrongCount);

            BigDecimal percentage = BigDecimal.ZERO;
            if (totalQuestions > 0) {
                percentage = BigDecimal.valueOf(correctCount)
                        .multiply(BigDecimal.valueOf(100))
                        .divide(BigDecimal.valueOf(totalQuestions), 2, RoundingMode.HALF_UP);
            }

            int passingScore = exam != null && exam.getPassingScore() != null ? exam.getPassingScore() : 70;
            String passStatus = percentage.compareTo(BigDecimal.valueOf(passingScore)) >= 0 ? "PASSED" : "FAILED";

            Integer timeTakenSeconds = session.getTimeSpentSeconds();
            if (timeTakenSeconds == null && session.getStartTime() != null && endTime != null) {
                timeTakenSeconds = (int) java.time.Duration.between(session.getStartTime(), endTime).getSeconds();
            }

            Long certificationId = session.getCertification() != null ? session.getCertification().getId() : null;
            LocalDateTime completedAt = endTime != null ? endTime : LocalDateTime.now();

            ExamResult examResult = ExamResult.builder()
                    .sessionId(sessionId)
                    .userId(session.getUserId())
                    .examId(exam != null ? exam.getId() : null)
                    .certificationId(certificationId)
                    .totalQuestions(totalQuestions)
                    .correctAnswers(correctCount)
                    .wrongAnswers(wrongCount)
                    .unanswered(unanswered)
                    .score(BigDecimal.valueOf(correctCount))
                    .percentage(percentage)
                    .passStatus(passStatus)
                    .passingScore(passingScore)
                    .mode(session.getMode())
                    .timeTakenSeconds(timeTakenSeconds)
                    .completedAt(completedAt)
                    .build();

            examResult = examResultRepository.save(examResult);
            List<ExamResultResponse.TopicPerformanceData> topicPerformanceDataList =
                    calculateAndSaveTopicPerformances(examResult, answers);

            List<ExamResultResponse.QuestionResultData> questionResultDataList =
                    createAndSaveQuestionResults(examResult, answers);

            return ExamResultResponse.builder()
                    .resultId(examResult.getId())
                    .sessionId(sessionId)
                    .userId(session.getUserId())
                    .examId(exam != null ? exam.getId() : null)
                    .examTitle(session.getExamTitle())
                    .totalQuestions(totalQuestions)
                    .correctAnswers(correctCount)
                    .wrongAnswers(wrongCount)
                    .unanswered(unanswered)
                    .score(BigDecimal.valueOf(correctCount))
                    .percentage(percentage)
                    .passStatus(passStatus)
                    .mode(session.getMode())
                    .passingScore(passingScore)
                    .timeTakenSeconds(timeTakenSeconds)
                    .completedAt(examResult.getCompletedAt())
                    .topicPerformances(topicPerformanceDataList)
                    .questionResults(questionResultDataList)
                    .success(true)
                    .build();

        } catch (Exception e) {
            log.error("Failed to process completed exam for session {}", sessionId, e);
            throw new IllegalStateException("Failed to calculate results for session " + sessionId, e);
        }
    }

    /**
     * Calculate topic-wise performance and save TopicPerformance records.
     */
    private List<ExamResultResponse.TopicPerformanceData> calculateAndSaveTopicPerformances(
            ExamResult examResult, List<UserAnswer> answers) {

        if (answers == null || answers.isEmpty()) {
            return new ArrayList<>();
        }

        Map<Long, List<UserAnswer>> answersByTopic = answers.stream()
                .filter(a -> topicIdOf(a) != null)
                .collect(Collectors.groupingBy(ExamResultProcessingService::topicIdOf));

        List<ExamResultResponse.TopicPerformanceData> topicPerformanceDataList = new ArrayList<>();
        List<TopicPerformance> topicPerformanceEntities = new ArrayList<>();

        for (Map.Entry<Long, List<UserAnswer>> entry : answersByTopic.entrySet()) {
            Long topicId = entry.getKey();
            List<UserAnswer> topicAnswers = entry.getValue();

            int totalInTopic = topicAnswers.size();
            int correctInTopic = (int) topicAnswers.stream()
                    .filter(a -> Boolean.TRUE.equals(a.getIsCorrect()))
                    .count();

            BigDecimal topicPercentage = BigDecimal.ZERO;
            if (totalInTopic > 0) {
                topicPercentage = BigDecimal.valueOf(correctInTopic)
                        .multiply(BigDecimal.valueOf(100))
                        .divide(BigDecimal.valueOf(totalInTopic), 2, RoundingMode.HALF_UP);
            }

            String topicName = topicNameOf(topicAnswers.getFirst());

            topicPerformanceEntities.add(TopicPerformance.builder()
                    .examResult(examResult)
                    .topicId(topicId)
                    .topicName(topicName)
                    .totalQuestions(totalInTopic)
                    .correctAnswers(correctInTopic)
                    .percentage(topicPercentage)
                    .build());

            topicPerformanceDataList.add(ExamResultResponse.TopicPerformanceData.builder()
                    .topicId(topicId)
                    .topicName(topicName)
                    .totalQuestions(totalInTopic)
                    .correctAnswers(correctInTopic)
                    .percentage(topicPercentage)
                    .build());
        }

        topicPerformanceRepository.saveAll(topicPerformanceEntities);
        return topicPerformanceDataList;
    }

    /**
     * Create QuestionResult records for each answer.
     */
    private List<ExamResultResponse.QuestionResultData> createAndSaveQuestionResults(
            ExamResult examResult, List<UserAnswer> answers) {

        if (answers == null || answers.isEmpty()) {
            return new ArrayList<>();
        }

        List<ExamResultResponse.QuestionResultData> questionResultDataList = new ArrayList<>();
        List<QuestionResult> questionResultEntities = new ArrayList<>();

        for (UserAnswer answer : answers) {
            Question question = answer.getQuestion();
            String reference = question.getReferenceUrl();

            questionResultEntities.add(QuestionResult.builder()
                    .examResult(examResult)
                    .questionId(question.getId())
                    .topicId(topicIdOf(answer))
                    .topicName(topicNameOf(answer))
                    .difficulty(question.getDifficulty())
                    .questionContent(question.getContent())
                    .userAnswerIds(answer.getSelectedOptionIds())
                    .correctAnswerIds(answer.getCorrectOptionIds())
                    .isCorrect(answer.getIsCorrect())
                    .timeSpentSeconds(answer.getTimeSpentSeconds())
                    .explanation(question.getExplanation())
                    .referenceLinks(reference)
                    .wasFlagged(Boolean.TRUE.equals(answer.getIsFlagged()))
                    .build());

            questionResultDataList.add(ExamResultResponse.QuestionResultData.builder()
                    .questionId(question.getId())
                    .questionText(question.getContent())
                    .userAnswerIds(answer.getSelectedOptionIds())
                    .correctAnswerIds(answer.getCorrectOptionIds())
                    .isCorrect(answer.getIsCorrect())
                    .timeSpentSeconds(answer.getTimeSpentSeconds())
                    .explanation(question.getExplanation())
                    .reference(reference)
                    .build());
        }

        questionResultRepository.saveAll(questionResultEntities);
        return questionResultDataList;
    }

    private static Long topicIdOf(UserAnswer answer) {
        Question question = answer.getQuestion();
        return question.getDomain() != null ? question.getDomain().getId() : null;
    }

    private static String topicNameOf(UserAnswer answer) {
        Question question = answer.getQuestion();
        return question.getDomain() != null ? question.getDomain().getName() : null;
    }

    /**
     * Build response from existing ExamResult (for idempotency).
     */
    private ExamResultResponse buildResponseFromExistingResult(ExamResult examResult) {
        List<TopicPerformance> topicPerformances = topicPerformanceRepository.findByExamResultId(examResult.getId());
        List<ExamResultResponse.TopicPerformanceData> topicPerformanceDataList = topicPerformances.stream()
                .map(tp -> ExamResultResponse.TopicPerformanceData.builder()
                        .topicId(tp.getTopicId())
                        .topicName(tp.getTopicName())
                        .totalQuestions(tp.getTotalQuestions())
                        .correctAnswers(tp.getCorrectAnswers())
                        .percentage(tp.getPercentage())
                        .build())
                .toList();

        List<QuestionResult> questionResults = questionResultRepository.findByExamResultId(examResult.getId());
        List<ExamResultResponse.QuestionResultData> questionResultDataList = questionResults.stream()
                .map(qr -> ExamResultResponse.QuestionResultData.builder()
                        .questionId(qr.getQuestionId())
                        .userAnswerIds(qr.getUserAnswerIds())
                        .correctAnswerIds(qr.getCorrectAnswerIds())
                        .isCorrect(qr.getIsCorrect())
                        .timeSpentSeconds(qr.getTimeSpentSeconds())
                        .build())
                .toList();

        return ExamResultResponse.builder()
                .resultId(examResult.getId())
                .sessionId(examResult.getSessionId())
                .userId(examResult.getUserId())
                .examId(examResult.getExamId())
                .totalQuestions(examResult.getTotalQuestions())
                .correctAnswers(examResult.getCorrectAnswers())
                .wrongAnswers(examResult.getWrongAnswers())
                .unanswered(examResult.getUnanswered())
                .score(examResult.getScore())
                .percentage(examResult.getPercentage())
                .passStatus(examResult.getPassStatus())
                .timeTakenSeconds(examResult.getTimeTakenSeconds())
                .completedAt(examResult.getCompletedAt())
                .topicPerformances(topicPerformanceDataList)
                .questionResults(questionResultDataList)
                .success(true)
                .build();
    }
}

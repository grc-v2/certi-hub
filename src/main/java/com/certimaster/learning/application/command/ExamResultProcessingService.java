package com.certimaster.learning.application.command;

import com.certimaster.learning.adapter.web.response.ExamResultResponse;
import com.certimaster.learning.domain.entity.ExamResult;
import com.certimaster.learning.domain.entity.QuestionResult;
import com.certimaster.learning.domain.entity.TopicPerformance;
import com.certimaster.learning.adapter.persistence.ExamResultWriteRepository;
import com.certimaster.learning.adapter.persistence.QuestionResultRepository;
import com.certimaster.learning.adapter.persistence.TopicPerformanceRepository;
import com.certimaster.learning.domain.event.ExamCompletedEvent;
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
 * Processes completed exam events and persists detailed result records.
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
    public ExamResultResponse processCompletedExam(ExamCompletedEvent event) {
        try {
            var existingResult = examResultRepository.findBySessionId(event.getSessionId());
            if (existingResult.isPresent()) {
                log.warn("Result already exists for session {}, returning existing result",
                        event.getSessionId());
                return buildResponseFromExistingResult(existingResult.get());
            }

            int totalQuestions = event.getTotalQuestions() != null ? event.getTotalQuestions() : 0;
            int correctCount = event.getCorrectCount() != null ? event.getCorrectCount() : 0;
            int wrongCount = event.getWrongCount() != null ? event.getWrongCount() : 0;
            int unanswered = totalQuestions - (correctCount + wrongCount);

            BigDecimal percentage = BigDecimal.ZERO;
            if (totalQuestions > 0) {
                percentage = BigDecimal.valueOf(correctCount)
                        .multiply(BigDecimal.valueOf(100))
                        .divide(BigDecimal.valueOf(totalQuestions), 2, RoundingMode.HALF_UP);
            }

            int passingScore = event.getPassingScore() != null ? event.getPassingScore() : 70;
            String passStatus = percentage.compareTo(BigDecimal.valueOf(passingScore)) >= 0 ? "PASSED" : "FAILED";

            Integer timeTakenSeconds = event.getTimeSpentSeconds();
            if (timeTakenSeconds == null && event.getStartTime() != null && event.getEndTime() != null) {
                timeTakenSeconds = (int) java.time.Duration.between(event.getStartTime(), event.getEndTime()).getSeconds();
            }

            ExamResult examResult = ExamResult.builder()
                    .sessionId(event.getSessionId())
                    .userId(event.getUserId())
                    .examId(event.getExamId())
                    .certificationId(event.getCertificationId())
                    .totalQuestions(totalQuestions)
                    .correctAnswers(correctCount)
                    .wrongAnswers(wrongCount)
                    .unanswered(unanswered)
                    .score(BigDecimal.valueOf(correctCount))
                    .percentage(percentage)
                    .passStatus(passStatus)
                    .passingScore(passingScore)
                    .mode(event.getMode())
                    .timeTakenSeconds(timeTakenSeconds)
                    .completedAt(event.getEndTime() != null ? event.getEndTime() : LocalDateTime.now())
                    .build();

            examResult = examResultRepository.save(examResult);
            List<ExamResultResponse.TopicPerformanceData> topicPerformanceDataList =
                    calculateAndSaveTopicPerformances(examResult, event.getAnswers());

            List<ExamResultResponse.QuestionResultData> questionResultDataList =
                    createAndSaveQuestionResults(examResult, event.getAnswers());

            return ExamResultResponse.builder()
                    .resultId(examResult.getId())
                    .sessionId(event.getSessionId())
                    .userId(event.getUserId())
                    .examId(event.getExamId())
                    .examTitle(event.getExamTitle())
                    .totalQuestions(totalQuestions)
                    .correctAnswers(correctCount)
                    .wrongAnswers(wrongCount)
                    .unanswered(unanswered)
                    .score(BigDecimal.valueOf(correctCount))
                    .percentage(percentage)
                    .passStatus(passStatus)
                    .mode(event.getMode())
                    .passingScore(passingScore)
                    .timeTakenSeconds(timeTakenSeconds)
                    .completedAt(examResult.getCompletedAt())
                    .topicPerformances(topicPerformanceDataList)
                    .questionResults(questionResultDataList)
                    .success(true)
                    .build();

        } catch (Exception e) {
            log.error("Failed to process completed exam for session {}", event.getSessionId(), e);
            throw new IllegalStateException("Failed to calculate results for session " + event.getSessionId(), e);
        }
    }

    /**
     * Calculate topic-wise performance and save TopicPerformance records.
     */
    private List<ExamResultResponse.TopicPerformanceData> calculateAndSaveTopicPerformances(
            ExamResult examResult, List<ExamCompletedEvent.UserAnswerData> answers) {

        if (answers == null || answers.isEmpty()) {
            return new ArrayList<>();
        }

        Map<Long, List<ExamCompletedEvent.UserAnswerData>> answersByTopic = answers.stream()
                .filter(a -> a.getTopicId() != null)
                .collect(Collectors.groupingBy(ExamCompletedEvent.UserAnswerData::getTopicId));

        List<ExamResultResponse.TopicPerformanceData> topicPerformanceDataList = new ArrayList<>();
        List<TopicPerformance> topicPerformanceEntities = new ArrayList<>();

        for (Map.Entry<Long, List<ExamCompletedEvent.UserAnswerData>> entry : answersByTopic.entrySet()) {
            Long topicId = entry.getKey();
            List<ExamCompletedEvent.UserAnswerData> topicAnswers = entry.getValue();

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

            String topicName = topicAnswers.getFirst().getTopicName();

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
            ExamResult examResult, List<ExamCompletedEvent.UserAnswerData> answers) {

        if (answers == null || answers.isEmpty()) {
            return new ArrayList<>();
        }

        List<ExamResultResponse.QuestionResultData> questionResultDataList = new ArrayList<>();
        List<QuestionResult> questionResultEntities = new ArrayList<>();

        for (ExamCompletedEvent.UserAnswerData answer : answers) {
            questionResultEntities.add(QuestionResult.builder()
                    .examResult(examResult)
                    .questionId(answer.getQuestionId())
                    .topicId(answer.getTopicId())
                    .topicName(answer.getTopicName())
                    .difficulty(answer.getDifficulty())
                    .questionContent(answer.getQuestionText())
                    .userAnswerIds(answer.getSelectedOptionIds())
                    .correctAnswerIds(answer.getCorrectOptionIds())
                    .isCorrect(answer.getIsCorrect())
                    .timeSpentSeconds(answer.getTimeSpentSeconds())
                    .explanation(answer.getExplanation())
                    .referenceLinks(answer.getReference())
                    .wasFlagged(Boolean.TRUE.equals(answer.getIsFlagged()))
                    .build());

            questionResultDataList.add(ExamResultResponse.QuestionResultData.builder()
                    .questionId(answer.getQuestionId())
                    .questionText(answer.getQuestionText())
                    .userAnswerIds(answer.getSelectedOptionIds())
                    .correctAnswerIds(answer.getCorrectOptionIds())
                    .isCorrect(answer.getIsCorrect())
                    .timeSpentSeconds(answer.getTimeSpentSeconds())
                    .explanation(answer.getExplanation())
                    .reference(answer.getReference())
                    .build());
        }

        questionResultRepository.saveAll(questionResultEntities);
        return questionResultDataList;
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

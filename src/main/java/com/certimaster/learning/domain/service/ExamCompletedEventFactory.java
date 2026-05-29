package com.certimaster.learning.domain.service;

import com.certimaster.learning.domain.entity.Exam;
import com.certimaster.learning.domain.entity.UserAnswer;
import com.certimaster.learning.domain.entity.UserExamSession;
import com.certimaster.learning.domain.entity.Question;
import com.certimaster.learning.domain.event.ExamCompletedEvent;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
class ExamCompletedEventFactory {

    ExamCompletedEvent create(UserExamSession session, List<UserAnswer> userAnswers, LocalDateTime endTime) {
        Exam exam = session.getExam();
        List<ExamCompletedEvent.UserAnswerData> answerDataList = userAnswers.stream()
                .map(this::toUserAnswerData)
                .toList();

        return ExamCompletedEvent.builder()
                .sessionId(session.getId())
                .userId(session.getUserId())
                .examId(exam != null ? exam.getId() : null)
                .certificationId(session.getCertification() != null ? session.getCertification().getId() : null)
                .mode(session.getMode())
                .examTitle(session.getExamTitle())
                .totalQuestions(session.getTotalQuestions())
                .durationMinutes(session.getDurationMinutes())
                .passingScore(exam != null ? exam.getPassingScore() : null)
                .startTime(session.getStartTime())
                .endTime(endTime)
                .answeredCount(session.getAnsweredCount())
                .correctCount(session.getCorrectCount())
                .wrongCount(session.getWrongCount())
                .flaggedCount(session.getFlaggedCount())
                .timeSpentSeconds(session.getTimeSpentSeconds())
                .answers(answerDataList)
                .eventTime(LocalDateTime.now())
                .build();
    }

    private ExamCompletedEvent.UserAnswerData toUserAnswerData(UserAnswer userAnswer) {
        Question question = userAnswer.getQuestion();

        return ExamCompletedEvent.UserAnswerData.builder()
                .questionId(question.getId())
                .topicId(question.getDomain() != null ? question.getDomain().getId() : null)
                .topicName(question.getDomain() != null ? question.getDomain().getName() : null)
                .difficulty(question.getDifficulty())
                .selectedOptionIds(userAnswer.getSelectedOptionIds())
                .correctOptionIds(userAnswer.getCorrectOptionIds())
                .isCorrect(userAnswer.getIsCorrect())
                .isFlagged(userAnswer.getIsFlagged())
                .timeSpentSeconds(userAnswer.getTimeSpentSeconds())
                .answeredAt(userAnswer.getAnsweredAt())
                .questionText(question.getContent())
                .explanation(question.getExplanation())
                .reference(question.getReferenceUrl())
                .build();
    }
}

package com.certimaster.learning.domain.entity;

import com.certimaster.learning.domain.model.Grade;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class UserExamSessionRecordAnswerTest {

    // Question with option 1 correct, option 2 wrong.
    private static Question question() {
        QuestionOption correct = QuestionOption.builder().content("a").isCorrect(true).orderIndex(1).build();
        correct.setId(1L);
        QuestionOption wrong = QuestionOption.builder().content("b").isCorrect(false).orderIndex(2).build();
        wrong.setId(2L);
        return Question.builder().questionOptions(new HashSet<>(List.of(correct, wrong))).build();
    }

    private static UserExamSession session(int answered, int correct, int wrong,
                                           int unanswered, int flagged, int timeSpent) {
        return UserExamSession.builder()
                .mode("PRACTICE")
                .totalQuestions(3)
                .answeredCount(answered)
                .correctCount(correct)
                .wrongCount(wrong)
                .unansweredCount(unanswered)
                .flaggedCount(flagged)
                .timeSpentSeconds(timeSpent)
                .build();
    }

    private static UserAnswer freshAnswer(Question q) {
        return UserAnswer.builder().question(q).build();
    }

    private static UserAnswer priorAnswer(Question q, Long[] selected, boolean correct) {
        return UserAnswer.builder().question(q).selectedOptionIds(selected).isCorrect(correct).build();
    }

    @Test
    void firstCorrectAnswerIncrementsAnsweredAndCorrectAndDecrementsUnanswered() {
        Question q = question();
        UserExamSession session = session(0, 0, 0, 3, 0, 0);
        UserAnswer answer = freshAnswer(q);

        Grade grade = session.recordAnswer(answer, List.of(1L), 30, null);

        assertThat(grade.correct()).isTrue();
        assertThat(session.getAnsweredCount()).isEqualTo(1);
        assertThat(session.getUnansweredCount()).isEqualTo(2);
        assertThat(session.getCorrectCount()).isEqualTo(1);
        assertThat(session.getWrongCount()).isZero();
        assertThat(answer.getIsCorrect()).isTrue();
    }

    @Test
    void firstWrongAnswerIncrementsAnsweredAndWrong() {
        Question q = question();
        UserExamSession session = session(0, 0, 0, 3, 0, 0);
        UserAnswer answer = freshAnswer(q);

        session.recordAnswer(answer, List.of(2L), null, null);

        assertThat(session.getAnsweredCount()).isEqualTo(1);
        assertThat(session.getUnansweredCount()).isEqualTo(2);
        assertThat(session.getCorrectCount()).isZero();
        assertThat(session.getWrongCount()).isEqualTo(1);
    }

    @Test
    void reAnswerFromCorrectToWrongMovesTheCounterWithoutChangingAnsweredCount() {
        Question q = question();
        UserExamSession session = session(1, 1, 0, 2, 0, 0);
        UserAnswer answer = priorAnswer(q, new Long[]{1L}, true);

        session.recordAnswer(answer, List.of(2L), null, null);

        assertThat(session.getAnsweredCount()).isEqualTo(1);
        assertThat(session.getUnansweredCount()).isEqualTo(2);
        assertThat(session.getCorrectCount()).isZero();
        assertThat(session.getWrongCount()).isEqualTo(1);
    }

    @Test
    void reAnswerFromWrongToCorrectMovesTheCounterWithoutChangingAnsweredCount() {
        Question q = question();
        UserExamSession session = session(1, 0, 1, 2, 0, 0);
        UserAnswer answer = priorAnswer(q, new Long[]{2L}, false);

        session.recordAnswer(answer, List.of(1L), null, null);

        assertThat(session.getAnsweredCount()).isEqualTo(1);
        assertThat(session.getCorrectCount()).isEqualTo(1);
        assertThat(session.getWrongCount()).isZero();
    }

    @Test
    void flaggingOnIncrementsFlaggedCountAndMarksTheAnswer() {
        Question q = question();
        UserExamSession session = session(0, 0, 0, 3, 0, 0);
        UserAnswer answer = freshAnswer(q);

        session.recordAnswer(answer, List.of(1L), null, true);

        assertThat(session.getFlaggedCount()).isEqualTo(1);
        assertThat(answer.getIsFlagged()).isTrue();
    }

    @Test
    void flaggingOffDecrementsFlaggedCountAndClearsTheAnswer() {
        Question q = question();
        UserExamSession session = session(1, 1, 0, 2, 1, 0);
        UserAnswer answer = UserAnswer.builder()
                .question(q).selectedOptionIds(new Long[]{1L}).isCorrect(true).isFlagged(true).build();

        session.recordAnswer(answer, List.of(1L), null, false);

        assertThat(session.getFlaggedCount()).isZero();
        assertThat(answer.getIsFlagged()).isFalse();
    }

    @Test
    void nullFlagLeavesFlagStateUntouched() {
        Question q = question();
        UserExamSession session = session(1, 1, 0, 2, 1, 0);
        UserAnswer answer = UserAnswer.builder()
                .question(q).selectedOptionIds(new Long[]{1L}).isCorrect(true).isFlagged(true).build();

        session.recordAnswer(answer, List.of(1L), null, null);

        assertThat(session.getFlaggedCount()).isEqualTo(1);
        assertThat(answer.getIsFlagged()).isTrue();
    }

    @Test
    void timeSpentAccumulatesOnBothSessionAndAnswer() {
        Question q = question();
        UserExamSession session = session(0, 0, 0, 3, 0, 10);
        UserAnswer answer = UserAnswer.builder().question(q).timeSpentSeconds(10).build();

        session.recordAnswer(answer, List.of(1L), 5, null);

        assertThat(session.getTimeSpentSeconds()).isEqualTo(15);
        assertThat(answer.getTimeSpentSeconds()).isEqualTo(15);
    }

    @Test
    void returnsGradeCarryingTheCorrectOptionIds() {
        Question q = question();
        UserExamSession session = session(0, 0, 0, 3, 0, 0);

        Grade grade = session.recordAnswer(freshAnswer(q), List.of(2L), null, null);

        assertThat(grade.correct()).isFalse();
        assertThat(grade.correctOptionIds()).containsExactly(1L);
    }
}

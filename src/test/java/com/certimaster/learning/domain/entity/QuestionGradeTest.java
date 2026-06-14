package com.certimaster.learning.domain.entity;

import com.certimaster.learning.domain.model.Grade;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class QuestionGradeTest {

    private static QuestionOption option(long id, boolean correct) {
        QuestionOption option = QuestionOption.builder()
                .content("opt-" + id)
                .isCorrect(correct)
                .orderIndex((int) id)
                .build();
        option.setId(id);
        return option;
    }

    private static Question questionWithOptions(QuestionOption... options) {
        Question question = Question.builder()
                .questionOptions(new java.util.HashSet<>(List.of(options)))
                .build();
        return question;
    }

    @Test
    void singleChoiceCorrectWhenTheOnlyCorrectOptionIsSelected() {
        Question question = questionWithOptions(option(1L, true), option(2L, false), option(3L, false));

        Grade grade = question.grade(List.of(1L));

        assertThat(grade.correct()).isTrue();
        assertThat(grade.correctOptionIds()).containsExactly(1L);
    }

    @Test
    void singleChoiceWrongWhenAnIncorrectOptionIsSelected() {
        Question question = questionWithOptions(option(1L, true), option(2L, false), option(3L, false));

        Grade grade = question.grade(List.of(2L));

        assertThat(grade.correct()).isFalse();
        assertThat(grade.correctOptionIds()).containsExactly(1L);
    }

    @Test
    void multipleResponseCorrectWhenAllAndOnlyCorrectOptionsAreSelected() {
        Question question = questionWithOptions(option(1L, true), option(2L, true), option(3L, false));

        Grade grade = question.grade(List.of(1L, 2L));

        assertThat(grade.correct()).isTrue();
        assertThat(grade.correctOptionIds()).containsExactlyInAnyOrder(1L, 2L);
    }

    @Test
    void multipleResponseWrongWhenOnlyPartOfTheCorrectSetIsSelected() {
        Question question = questionWithOptions(option(1L, true), option(2L, true), option(3L, false));

        Grade grade = question.grade(List.of(1L));

        assertThat(grade.correct()).isFalse();
    }

    @Test
    void multipleResponseWrongWhenAnExtraIncorrectOptionIsSelected() {
        Question question = questionWithOptions(option(1L, true), option(2L, true), option(3L, false));

        Grade grade = question.grade(List.of(1L, 2L, 3L));

        assertThat(grade.correct()).isFalse();
    }

    @Test
    void emptySelectionIsWrongWhenThereIsACorrectOption() {
        Question question = questionWithOptions(option(1L, true), option(2L, false));

        Grade grade = question.grade(List.of());

        assertThat(grade.correct()).isFalse();
        assertThat(grade.correctOptionIds()).containsExactly(1L);
    }

    @Test
    void trueFalseCorrectWhenTheCorrectSideIsSelected() {
        Question question = questionWithOptions(option(1L, false), option(2L, true));

        Grade grade = question.grade(List.of(2L));

        assertThat(grade.correct()).isTrue();
        assertThat(grade.correctOptionIds()).containsExactly(2L);
    }
}

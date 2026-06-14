package com.certimaster.learning.domain.entity;

import com.certimaster.learning.domain.model.DomainScore;
import com.certimaster.learning.domain.model.SessionScore;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class UserExamSessionCompleteTest {

    private static final LocalDateTime END = LocalDateTime.of(2026, 6, 14, 10, 0);

    private static CertificationDomain domain(long id, String name) {
        CertificationDomain domain = CertificationDomain.builder().name(name).build();
        domain.setId(id);
        return domain;
    }

    private static Question question(CertificationDomain domain) {
        return Question.builder().domain(domain).build();
    }

    private static UserAnswer answer(Question question, boolean correct) {
        return UserAnswer.builder().question(question).isCorrect(correct).build();
    }

    private static UserExamSession.Builder sessionBuilder(int totalQuestions) {
        return UserExamSession.builder().mode("PRACTICE").totalQuestions(totalQuestions);
    }

    @Test
    void scorePercentageIsCorrectCountOverTotalRoundedToTwoPlaces() {
        CertificationDomain d = domain(1L, "Security");
        UserExamSession session = sessionBuilder(3).build();
        List<UserAnswer> answers = List.of(
                answer(question(d), true),
                answer(question(d), true),
                answer(question(d), false));

        SessionScore score = session.complete(answers, "COMPLETED", END);

        assertThat(score.score()).isEqualByComparingTo("2");
        assertThat(score.scorePercentage()).isEqualByComparingTo("66.67");
    }

    @Test
    void practiceSessionWithNoExamNeverPasses() {
        CertificationDomain d = domain(1L, "Security");
        UserExamSession session = sessionBuilder(2).build();
        List<UserAnswer> answers = List.of(answer(question(d), true), answer(question(d), true));

        SessionScore score = session.complete(answers, "COMPLETED", END);

        assertThat(score.scorePercentage()).isEqualByComparingTo("100.00");
        assertThat(score.passed()).isFalse();
    }

    @Test
    void examSessionPassesWhenPercentageMeetsThePassingScore() {
        CertificationDomain d = domain(1L, "Security");
        Exam exam = Exam.builder().passingScore(70).build();
        UserExamSession session = sessionBuilder(3).exam(exam).build();
        List<UserAnswer> answers = List.of(
                answer(question(d), true),
                answer(question(d), true),
                answer(question(d), true));

        SessionScore score = session.complete(answers, "COMPLETED", END);

        assertThat(score.passed()).isTrue();
    }

    @Test
    void examSessionFailsWhenPercentageIsBelowThePassingScore() {
        CertificationDomain d = domain(1L, "Security");
        Exam exam = Exam.builder().passingScore(70).build();
        UserExamSession session = sessionBuilder(3).exam(exam).build();
        List<UserAnswer> answers = List.of(
                answer(question(d), true),
                answer(question(d), true),
                answer(question(d), false));

        SessionScore score = session.complete(answers, "COMPLETED", END);

        assertThat(score.passed()).isFalse();
    }

    @Test
    void domainBreakdownTalliesPerCertificationDomain() {
        CertificationDomain security = domain(1L, "Security");
        CertificationDomain networking = domain(2L, "Networking");
        UserExamSession session = sessionBuilder(3).build();
        List<UserAnswer> answers = List.of(
                answer(question(security), true),
                answer(question(security), false),
                answer(question(networking), true));

        SessionScore score = session.complete(answers, "COMPLETED", END);

        assertThat(score.domainScores()).hasSize(2);
        DomainScore securityScore = score.domainScores().stream()
                .filter(s -> s.domainId().equals(1L)).findFirst().orElseThrow();
        DomainScore networkingScore = score.domainScores().stream()
                .filter(s -> s.domainId().equals(2L)).findFirst().orElseThrow();

        assertThat(securityScore.totalQuestions()).isEqualTo(2);
        assertThat(securityScore.correctCount()).isEqualTo(1);
        assertThat(securityScore.scorePercentage()).isEqualByComparingTo("50.00");
        assertThat(networkingScore.totalQuestions()).isEqualTo(1);
        assertThat(networkingScore.correctCount()).isEqualTo(1);
        assertThat(networkingScore.scorePercentage()).isEqualByComparingTo("100.00");
    }

    @Test
    void completeAppliesTargetStatusAndEndTimeAndScoreToTheSession() {
        CertificationDomain d = domain(1L, "Security");
        UserExamSession session = sessionBuilder(2).build();
        List<UserAnswer> answers = List.of(answer(question(d), true), answer(question(d), false));

        session.complete(answers, "TIMED_OUT", END);

        assertThat(session.getStatus()).isEqualTo("TIMED_OUT");
        assertThat(session.getEndTime()).isEqualTo(END);
        assertThat(session.getScore()).isEqualByComparingTo("1");
        assertThat(session.getScorePercentage()).isEqualByComparingTo("50.00");
        assertThat(session.getDomainScores()).hasSize(1);
    }

    @Test
    void zeroTotalQuestionsYieldsZeroPercentageWithoutDividingByZero() {
        UserExamSession session = sessionBuilder(0).build();

        SessionScore score = session.complete(List.of(), "COMPLETED", END);

        assertThat(score.score()).isEqualByComparingTo("0");
        assertThat(score.scorePercentage()).isEqualByComparingTo(BigDecimal.ZERO);
        assertThat(score.domainScores()).isEmpty();
    }
}

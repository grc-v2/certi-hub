package com.certimaster.learning.domain.entity;

import com.certimaster.shared.exception.BusinessException;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class UserExamSessionLifecycleTest {

    private static UserExamSession.Builder session(String mode, String status) {
        return UserExamSession.builder().mode(mode).status(status).totalQuestions(10).timeSpentSeconds(0);
    }

    // ---- pause ----

    @Test
    void pauseMovesAnInProgressPracticeSessionToPaused() {
        UserExamSession s = session("PRACTICE", "IN_PROGRESS").build();

        s.pause(null);

        assertThat(s.getStatus()).isEqualTo("PAUSED");
    }

    @Test
    void pauseAccumulatesTimeSpent() {
        UserExamSession s = session("PRACTICE", "IN_PROGRESS").timeSpentSeconds(40).build();

        s.pause(20);

        assertThat(s.getTimeSpentSeconds()).isEqualTo(60);
        assertThat(s.getStatus()).isEqualTo("PAUSED");
    }

    @Test
    void pauseRejectsExamSessions() {
        UserExamSession s = session("EXAM", "IN_PROGRESS").build();

        assertThatThrownBy(() -> s.pause(null))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("Only practice sessions can be paused");
        assertThat(s.getStatus()).isEqualTo("IN_PROGRESS");
    }

    @Test
    void pauseRejectsSessionsThatAreNotInProgress() {
        UserExamSession s = session("PRACTICE", "PAUSED").build();

        assertThatThrownBy(() -> s.pause(null))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("Only active sessions can be paused");
    }

    // ---- resume ----

    @Test
    void resumeMovesAPausedSessionBackToInProgress() {
        UserExamSession s = session("PRACTICE", "PAUSED").build();

        s.resume();

        assertThat(s.getStatus()).isEqualTo("IN_PROGRESS");
    }

    @Test
    void resumeRejectsSessionsThatAreNotPaused() {
        UserExamSession s = session("PRACTICE", "IN_PROGRESS").build();

        assertThatThrownBy(s::resume)
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("Only paused sessions can be resumed");
    }

    // ---- abandon ----

    @Test
    void abandonMarksAnInProgressSessionAbandonedAndStampsEndTime() {
        UserExamSession s = session("EXAM", "IN_PROGRESS").build();

        s.abandon();

        assertThat(s.getStatus()).isEqualTo("ABANDONED");
        assertThat(s.getEndTime()).isNotNull();
    }

    @Test
    void abandonRejectsSessionsThatAreNoLongerActive() {
        UserExamSession s = session("EXAM", "COMPLETED").build();

        assertThatThrownBy(s::abandon)
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("This session is no longer active");
    }

    // ---- ensureActive ----

    @Test
    void ensureActiveThrowsWhenNotInProgress() {
        UserExamSession s = session("EXAM", "TIMED_OUT").build();

        assertThatThrownBy(s::ensureActive)
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("This session is no longer active");
    }

    // ---- deadline ----

    @Test
    void untimedSessionIsNeverPastItsDeadline() {
        UserExamSession s = session("PRACTICE", "IN_PROGRESS")
                .startTime(LocalDateTime.now().minusHours(2))
                .build(); // no durationMinutes -> untimed

        assertThat(s.isPastDeadline()).isFalse();
        s.ensureWithinDeadline(); // does not throw
    }

    @Test
    void timedSessionPastItsDeadlineIsDetectedAndRejected() {
        UserExamSession s = session("EXAM", "IN_PROGRESS")
                .startTime(LocalDateTime.now().minusMinutes(60))
                .durationMinutes(30)
                .build();

        assertThat(s.isPastDeadline()).isTrue();
        assertThatThrownBy(s::ensureWithinDeadline)
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("Session deadline has passed");
    }

    @Test
    void timedSessionWithinItsDeadlineIsAllowed() {
        UserExamSession s = session("EXAM", "IN_PROGRESS")
                .startTime(LocalDateTime.now())
                .durationMinutes(30)
                .build();

        assertThat(s.isPastDeadline()).isFalse();
        s.ensureWithinDeadline(); // does not throw
    }
}

package com.certimaster.learning.domain.entity;

import com.certimaster.shared.entity.BaseEntity;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Entity representing the final result of an exam session.
 */
@Entity
@Table(name = "exam_results")
public class ExamResult extends BaseEntity {

    @Column(name = "session_id", nullable = false)
    private Long sessionId;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "exam_id")
    private Long examId;

    @Column(name = "certification_id", nullable = false)
    private Long certificationId;

    @Column(name = "total_questions", nullable = false)
    private Integer totalQuestions;

    @Column(name = "correct_answers", nullable = false)
    private Integer correctAnswers;

    @Column(name = "wrong_answers", nullable = false)
    private Integer wrongAnswers;

    @Column(name = "unanswered", nullable = false)
    private Integer unanswered;

    @Column(name = "score", nullable = false, precision = 5, scale = 2)
    private BigDecimal score;

    @Column(name = "percentage", nullable = false, precision = 5, scale = 2)
    private BigDecimal percentage;

    @Column(name = "pass_status", nullable = false, length = 20)
    private String passStatus; // PASSED, FAILED

    @Column(name = "passing_score", nullable = false)
    private Integer passingScore;

    @Column(name = "mode", length = 20)
    private String mode;

    @Column(name = "time_taken_seconds")
    private Integer timeTakenSeconds;

    @Column(name = "completed_at", nullable = false)
    private LocalDateTime completedAt;

    @OneToMany(mappedBy = "examResult", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<TopicPerformance> topicPerformances = new ArrayList<>();

    @OneToMany(mappedBy = "examResult", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<QuestionResult> questionResults = new ArrayList<>();

    public ExamResult() {
        super();
    }

    public ExamResult(Long sessionId, Long userId, Long examId, Long certificationId,
                      Integer totalQuestions, Integer correctAnswers, Integer wrongAnswers,
                      Integer unanswered, BigDecimal score, BigDecimal percentage,
                      String passStatus, Integer timeTakenSeconds, LocalDateTime completedAt,
                      List<TopicPerformance> topicPerformances, List<QuestionResult> questionResults) {
        super();
        this.sessionId = sessionId;
        this.userId = userId;
        this.examId = examId;
        this.certificationId = certificationId;
        this.totalQuestions = totalQuestions;
        this.correctAnswers = correctAnswers;
        this.wrongAnswers = wrongAnswers;
        this.unanswered = unanswered;
        this.score = score;
        this.percentage = percentage;
        this.passStatus = passStatus;
        this.timeTakenSeconds = timeTakenSeconds;
        this.completedAt = completedAt;
        this.topicPerformances = topicPerformances;
        this.questionResults = questionResults;
    }

    public Long getSessionId() { return sessionId; }
    public void setSessionId(Long sessionId) { this.sessionId = sessionId; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public Long getExamId() { return examId; }
    public void setExamId(Long examId) { this.examId = examId; }

    public Long getCertificationId() { return certificationId; }
    public void setCertificationId(Long certificationId) { this.certificationId = certificationId; }

    public Integer getTotalQuestions() { return totalQuestions; }
    public void setTotalQuestions(Integer totalQuestions) { this.totalQuestions = totalQuestions; }

    public Integer getCorrectAnswers() { return correctAnswers; }
    public void setCorrectAnswers(Integer correctAnswers) { this.correctAnswers = correctAnswers; }

    public Integer getWrongAnswers() { return wrongAnswers; }
    public void setWrongAnswers(Integer wrongAnswers) { this.wrongAnswers = wrongAnswers; }

    public Integer getUnanswered() { return unanswered; }
    public void setUnanswered(Integer unanswered) { this.unanswered = unanswered; }

    public BigDecimal getScore() { return score; }
    public void setScore(BigDecimal score) { this.score = score; }

    public BigDecimal getPercentage() { return percentage; }
    public void setPercentage(BigDecimal percentage) { this.percentage = percentage; }

    public String getPassStatus() { return passStatus; }
    public void setPassStatus(String passStatus) { this.passStatus = passStatus; }

    public Integer getPassingScore() { return passingScore; }
    public void setPassingScore(Integer passingScore) { this.passingScore = passingScore; }

    public String getMode() { return mode; }
    public void setMode(String mode) { this.mode = mode; }

    public Integer getTimeTakenSeconds() { return timeTakenSeconds; }
    public void setTimeTakenSeconds(Integer timeTakenSeconds) { this.timeTakenSeconds = timeTakenSeconds; }

    public LocalDateTime getCompletedAt() { return completedAt; }
    public void setCompletedAt(LocalDateTime completedAt) { this.completedAt = completedAt; }

    public List<TopicPerformance> getTopicPerformances() { return topicPerformances; }
    public void setTopicPerformances(List<TopicPerformance> topicPerformances) { this.topicPerformances = topicPerformances; }

    public List<QuestionResult> getQuestionResults() { return questionResults; }
    public void setQuestionResults(List<QuestionResult> questionResults) { this.questionResults = questionResults; }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private Long sessionId;
        private Long userId;
        private Long examId;
        private Long certificationId;
        private Integer totalQuestions;
        private Integer correctAnswers;
        private Integer wrongAnswers;
        private Integer unanswered;
        private BigDecimal score;
        private BigDecimal percentage;
        private String passStatus;
        private Integer passingScore;
        private String mode;
        private Integer timeTakenSeconds;
        private LocalDateTime completedAt;
        private List<TopicPerformance> topicPerformances = new ArrayList<>();
        private List<QuestionResult> questionResults = new ArrayList<>();

        public Builder sessionId(Long v) { this.sessionId = v; return this; }
        public Builder userId(Long v) { this.userId = v; return this; }
        public Builder examId(Long v) { this.examId = v; return this; }
        public Builder certificationId(Long v) { this.certificationId = v; return this; }
        public Builder totalQuestions(Integer v) { this.totalQuestions = v; return this; }
        public Builder correctAnswers(Integer v) { this.correctAnswers = v; return this; }
        public Builder wrongAnswers(Integer v) { this.wrongAnswers = v; return this; }
        public Builder unanswered(Integer v) { this.unanswered = v; return this; }
        public Builder score(BigDecimal v) { this.score = v; return this; }
        public Builder percentage(BigDecimal v) { this.percentage = v; return this; }
        public Builder passStatus(String v) { this.passStatus = v; return this; }
        public Builder passingScore(Integer v) { this.passingScore = v; return this; }
        public Builder mode(String v) { this.mode = v; return this; }
        public Builder timeTakenSeconds(Integer v) { this.timeTakenSeconds = v; return this; }
        public Builder completedAt(LocalDateTime v) { this.completedAt = v; return this; }
        public Builder topicPerformances(List<TopicPerformance> v) { this.topicPerformances = v; return this; }
        public Builder questionResults(List<QuestionResult> v) { this.questionResults = v; return this; }

        public ExamResult build() {
            ExamResult o = new ExamResult();
            o.sessionId = this.sessionId;
            o.userId = this.userId;
            o.examId = this.examId;
            o.certificationId = this.certificationId;
            o.totalQuestions = this.totalQuestions;
            o.correctAnswers = this.correctAnswers;
            o.wrongAnswers = this.wrongAnswers;
            o.unanswered = this.unanswered;
            o.score = this.score;
            o.percentage = this.percentage;
            o.passStatus = this.passStatus;
            o.passingScore = this.passingScore;
            o.mode = this.mode;
            o.timeTakenSeconds = this.timeTakenSeconds;
            o.completedAt = this.completedAt;
            o.topicPerformances = this.topicPerformances;
            o.questionResults = this.questionResults;
            return o;
        }
    }
}

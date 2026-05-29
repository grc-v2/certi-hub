package com.certimaster.learning.domain.entity;

import com.certimaster.shared.entity.BaseEntity;
import com.certimaster.learning.adapter.web.response.DomainScore;
import com.certimaster.learning.domain.entity.Certification;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "user_exam_sessions")
public class UserExamSession extends BaseEntity {

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "exam_id")
    private Exam exam;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "certification_id")
    private Certification certification;

    @Column(name = "start_time", nullable = false)
    private LocalDateTime startTime;

    @Column(name = "end_time")
    private LocalDateTime endTime;

    @Column(name = "status", length = 20)
    private String status = "IN_PROGRESS";

    @Column(name = "mode", length = 20, nullable = false)
    private String mode;

    @Column(name = "exam_title", length = 500)
    private String examTitle;

    @Column(name = "total_questions", nullable = false)
    private Integer totalQuestions;

    @Column(name = "duration_minutes")
    private Integer durationMinutes;

    @Column(name = "answered_count")
    private Integer answeredCount = 0;

    @Column(name = "correct_count")
    private Integer correctCount = 0;

    @Column(name = "wrong_count")
    private Integer wrongCount = 0;

    @Column(name = "unanswered_count")
    private Integer unansweredCount;

    @Column(name = "flagged_count")
    private Integer flaggedCount = 0;

    @Column(name = "time_spent_seconds")
    private Integer timeSpentSeconds = 0;

    @Column(name = "score", precision = 10, scale = 2)
    private BigDecimal score;

    @Column(name = "score_percentage", precision = 5, scale = 2)
    private BigDecimal scorePercentage;

    @Column(name = "passed")
    private Boolean passed;

    @Column(name = "domain_scores", columnDefinition = "jsonb")
    @JdbcTypeCode(SqlTypes.JSON)
    private List<DomainScore> domainScores;

    @Column(name = "question_bank_id")
    private Long questionBankId;

    @Column(name = "domain_filter", columnDefinition = "jsonb")
    @JdbcTypeCode(SqlTypes.JSON)
    private List<Long> domainFilter;

    @Column(name = "difficulty_filter", columnDefinition = "jsonb")
    @JdbcTypeCode(SqlTypes.JSON)
    private List<String> difficultyFilter;

    @OneToMany(mappedBy = "userExamSession", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private Set<UserAnswer> userAnswers = new HashSet<>();

    public UserExamSession() {
        super();
    }

    public UserExamSession(Long userId, Exam exam, Certification certification,
                           LocalDateTime startTime, LocalDateTime endTime, String status,
                           String mode, String examTitle, Integer totalQuestions,
                           Integer durationMinutes, Integer answeredCount, Integer correctCount,
                           Integer wrongCount, Integer unansweredCount, Integer flaggedCount,
                           Integer timeSpentSeconds, Set<UserAnswer> userAnswers) {
        super();
        this.userId = userId;
        this.exam = exam;
        this.certification = certification;
        this.startTime = startTime;
        this.endTime = endTime;
        this.status = status;
        this.mode = mode;
        this.examTitle = examTitle;
        this.totalQuestions = totalQuestions;
        this.durationMinutes = durationMinutes;
        this.answeredCount = answeredCount;
        this.correctCount = correctCount;
        this.wrongCount = wrongCount;
        this.unansweredCount = unansweredCount;
        this.flaggedCount = flaggedCount;
        this.timeSpentSeconds = timeSpentSeconds;
        this.userAnswers = userAnswers;
    }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public Exam getExam() { return exam; }
    public void setExam(Exam exam) { this.exam = exam; }

    public Certification getCertification() { return certification; }
    public void setCertification(Certification certification) { this.certification = certification; }

    public LocalDateTime getStartTime() { return startTime; }
    public void setStartTime(LocalDateTime startTime) { this.startTime = startTime; }

    public LocalDateTime getEndTime() { return endTime; }
    public void setEndTime(LocalDateTime endTime) { this.endTime = endTime; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getMode() { return mode; }
    public void setMode(String mode) { this.mode = mode; }

    public String getExamTitle() { return examTitle; }
    public void setExamTitle(String examTitle) { this.examTitle = examTitle; }

    public Integer getTotalQuestions() { return totalQuestions; }
    public void setTotalQuestions(Integer totalQuestions) { this.totalQuestions = totalQuestions; }

    public Integer getDurationMinutes() { return durationMinutes; }
    public void setDurationMinutes(Integer durationMinutes) { this.durationMinutes = durationMinutes; }

    public Integer getAnsweredCount() { return answeredCount; }
    public void setAnsweredCount(Integer answeredCount) { this.answeredCount = answeredCount; }

    public Integer getCorrectCount() { return correctCount; }
    public void setCorrectCount(Integer correctCount) { this.correctCount = correctCount; }

    public Integer getWrongCount() { return wrongCount; }
    public void setWrongCount(Integer wrongCount) { this.wrongCount = wrongCount; }

    public Integer getUnansweredCount() { return unansweredCount; }
    public void setUnansweredCount(Integer unansweredCount) { this.unansweredCount = unansweredCount; }

    public Integer getFlaggedCount() { return flaggedCount; }
    public void setFlaggedCount(Integer flaggedCount) { this.flaggedCount = flaggedCount; }

    public Integer getTimeSpentSeconds() { return timeSpentSeconds; }
    public void setTimeSpentSeconds(Integer timeSpentSeconds) { this.timeSpentSeconds = timeSpentSeconds; }

    public BigDecimal getScore() { return score; }
    public void setScore(BigDecimal score) { this.score = score; }

    public BigDecimal getScorePercentage() { return scorePercentage; }
    public void setScorePercentage(BigDecimal scorePercentage) { this.scorePercentage = scorePercentage; }

    public Boolean getPassed() { return passed; }
    public void setPassed(Boolean passed) { this.passed = passed; }

    public List<DomainScore> getDomainScores() { return domainScores; }
    public void setDomainScores(List<DomainScore> domainScores) { this.domainScores = domainScores; }

    public Long getQuestionBankId() { return questionBankId; }
    public void setQuestionBankId(Long questionBankId) { this.questionBankId = questionBankId; }

    public List<Long> getDomainFilter() { return domainFilter; }
    public void setDomainFilter(List<Long> domainFilter) { this.domainFilter = domainFilter; }

    public List<String> getDifficultyFilter() { return difficultyFilter; }
    public void setDifficultyFilter(List<String> difficultyFilter) { this.difficultyFilter = difficultyFilter; }

    public Set<UserAnswer> getUserAnswers() { return userAnswers; }
    public void setUserAnswers(Set<UserAnswer> userAnswers) { this.userAnswers = userAnswers; }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private Long userId;
        private Exam exam;
        private Certification certification;
        private LocalDateTime startTime;
        private LocalDateTime endTime;
        private String status = "IN_PROGRESS";
        private String mode;
        private String examTitle;
        private Integer totalQuestions;
        private Integer durationMinutes;
        private Integer answeredCount = 0;
        private Integer correctCount = 0;
        private Integer wrongCount = 0;
        private Integer unansweredCount;
        private Integer flaggedCount = 0;
        private Integer timeSpentSeconds = 0;
        private BigDecimal score;
        private BigDecimal scorePercentage;
        private Boolean passed;
        private List<DomainScore> domainScores;
        private Long questionBankId;
        private List<Long> domainFilter;
        private List<String> difficultyFilter;
        private Set<UserAnswer> userAnswers = new HashSet<>();

        public Builder userId(Long v) { this.userId = v; return this; }
        public Builder exam(Exam v) { this.exam = v; return this; }
        public Builder certification(Certification v) { this.certification = v; return this; }
        public Builder startTime(LocalDateTime v) { this.startTime = v; return this; }
        public Builder endTime(LocalDateTime v) { this.endTime = v; return this; }
        public Builder status(String v) { this.status = v; return this; }
        public Builder mode(String v) { this.mode = v; return this; }
        public Builder examTitle(String v) { this.examTitle = v; return this; }
        public Builder totalQuestions(Integer v) { this.totalQuestions = v; return this; }
        public Builder durationMinutes(Integer v) { this.durationMinutes = v; return this; }
        public Builder answeredCount(Integer v) { this.answeredCount = v; return this; }
        public Builder correctCount(Integer v) { this.correctCount = v; return this; }
        public Builder wrongCount(Integer v) { this.wrongCount = v; return this; }
        public Builder unansweredCount(Integer v) { this.unansweredCount = v; return this; }
        public Builder flaggedCount(Integer v) { this.flaggedCount = v; return this; }
        public Builder timeSpentSeconds(Integer v) { this.timeSpentSeconds = v; return this; }
        public Builder score(BigDecimal v) { this.score = v; return this; }
        public Builder scorePercentage(BigDecimal v) { this.scorePercentage = v; return this; }
        public Builder passed(Boolean v) { this.passed = v; return this; }
        public Builder domainScores(List<DomainScore> v) { this.domainScores = v; return this; }
        public Builder questionBankId(Long v) { this.questionBankId = v; return this; }
        public Builder domainFilter(List<Long> v) { this.domainFilter = v; return this; }
        public Builder difficultyFilter(List<String> v) { this.difficultyFilter = v; return this; }
        public Builder userAnswers(Set<UserAnswer> v) { this.userAnswers = v; return this; }

        public UserExamSession build() {
            UserExamSession o = new UserExamSession();
            o.userId = this.userId;
            o.exam = this.exam;
            o.certification = this.certification;
            o.startTime = this.startTime;
            o.endTime = this.endTime;
            o.status = this.status;
            o.mode = this.mode;
            o.examTitle = this.examTitle;
            o.totalQuestions = this.totalQuestions;
            o.durationMinutes = this.durationMinutes;
            o.answeredCount = this.answeredCount;
            o.correctCount = this.correctCount;
            o.wrongCount = this.wrongCount;
            o.unansweredCount = this.unansweredCount;
            o.flaggedCount = this.flaggedCount;
            o.timeSpentSeconds = this.timeSpentSeconds;
            o.score = this.score;
            o.scorePercentage = this.scorePercentage;
            o.passed = this.passed;
            o.domainScores = this.domainScores;
            o.questionBankId = this.questionBankId;
            o.domainFilter = this.domainFilter;
            o.difficultyFilter = this.difficultyFilter;
            o.userAnswers = this.userAnswers;
            return o;
        }
    }
}

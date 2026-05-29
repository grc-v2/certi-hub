package com.certimaster.learning.domain.event;

import org.springframework.modulith.events.Externalized;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Event published when a user completes an exam session.
 * Contains full session data and all user answers for result calculation.
 * Sent from exam-service to result-service via Kafka.
 */
@Externalized("exam-completed::#{getSessionId()}")
public class ExamCompletedEvent implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private String eventId = UUID.randomUUID().toString();
    private Integer eventVersion = 1;

    /**
     * Session ID from exam-service.
     */
    private Long sessionId;

    /**
     * User ID.
     */
    private Long userId;

    /**
     * Exam ID.
     */
    private Long examId;

    /**
     * Certification ID.
     */
    private Long certificationId;

    /**
     * Exam mode (PRACTICE or TIMED).
     */
    private String mode;

    /**
     * Exam title.
     */
    private String examTitle;

    /**
     * Total number of questions in the exam.
     */
    private Integer totalQuestions;

    /**
     * Duration of the exam in minutes.
     */
    private Integer durationMinutes;

    /**
     * Passing score percentage.
     */
    private Integer passingScore;

    /**
     * Session start time.
     */
    private LocalDateTime startTime;

    /**
     * Session end time.
     */
    private LocalDateTime endTime;

    /**
     * Number of answered questions.
     */
    private Integer answeredCount;

    /**
     * Number of correct answers.
     */
    private Integer correctCount;

    /**
     * Number of wrong answers.
     */
    private Integer wrongCount;

    /**
     * Number of flagged questions.
     */
    private Integer flaggedCount;

    /**
     * Total time spent in seconds.
     */
    private Integer timeSpentSeconds;

    /**
     * List of all user answers with question metadata.
     */
    private List<UserAnswerData> answers;

    /**
     * Event timestamp.
     */
    private LocalDateTime eventTime = LocalDateTime.now();

    public ExamCompletedEvent() {
    }

    public ExamCompletedEvent(Long sessionId, Long userId, Long examId, Long certificationId,
                               String mode, String examTitle, Integer totalQuestions,
                               Integer durationMinutes, Integer passingScore,
                               LocalDateTime startTime, LocalDateTime endTime,
                               Integer answeredCount, Integer correctCount, Integer wrongCount,
                               Integer flaggedCount, Integer timeSpentSeconds,
                               List<UserAnswerData> answers, LocalDateTime eventTime) {
        this.sessionId = sessionId;
        this.userId = userId;
        this.examId = examId;
        this.certificationId = certificationId;
        this.mode = mode;
        this.examTitle = examTitle;
        this.totalQuestions = totalQuestions;
        this.durationMinutes = durationMinutes;
        this.passingScore = passingScore;
        this.startTime = startTime;
        this.endTime = endTime;
        this.answeredCount = answeredCount;
        this.correctCount = correctCount;
        this.wrongCount = wrongCount;
        this.flaggedCount = flaggedCount;
        this.timeSpentSeconds = timeSpentSeconds;
        this.answers = answers;
        this.eventTime = eventTime;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Long sessionId;
        private Long userId;
        private Long examId;
        private Long certificationId;
        private String mode;
        private String examTitle;
        private Integer totalQuestions;
        private Integer durationMinutes;
        private Integer passingScore;
        private LocalDateTime startTime;
        private LocalDateTime endTime;
        private Integer answeredCount;
        private Integer correctCount;
        private Integer wrongCount;
        private Integer flaggedCount;
        private Integer timeSpentSeconds;
        private List<UserAnswerData> answers;
        private String eventId = UUID.randomUUID().toString();
        private Integer eventVersion = 1;
        private LocalDateTime eventTime = LocalDateTime.now();

        public Builder sessionId(Long v) { this.sessionId = v; return this; }
        public Builder userId(Long v) { this.userId = v; return this; }
        public Builder examId(Long v) { this.examId = v; return this; }
        public Builder certificationId(Long v) { this.certificationId = v; return this; }
        public Builder mode(String v) { this.mode = v; return this; }
        public Builder examTitle(String v) { this.examTitle = v; return this; }
        public Builder totalQuestions(Integer v) { this.totalQuestions = v; return this; }
        public Builder durationMinutes(Integer v) { this.durationMinutes = v; return this; }
        public Builder passingScore(Integer v) { this.passingScore = v; return this; }
        public Builder startTime(LocalDateTime v) { this.startTime = v; return this; }
        public Builder endTime(LocalDateTime v) { this.endTime = v; return this; }
        public Builder answeredCount(Integer v) { this.answeredCount = v; return this; }
        public Builder correctCount(Integer v) { this.correctCount = v; return this; }
        public Builder wrongCount(Integer v) { this.wrongCount = v; return this; }
        public Builder flaggedCount(Integer v) { this.flaggedCount = v; return this; }
        public Builder timeSpentSeconds(Integer v) { this.timeSpentSeconds = v; return this; }
        public Builder answers(List<UserAnswerData> v) { this.answers = v; return this; }
        public Builder eventId(String v) { this.eventId = v; return this; }
        public Builder eventVersion(Integer v) { this.eventVersion = v; return this; }
        public Builder eventTime(LocalDateTime v) { this.eventTime = v; return this; }

        public ExamCompletedEvent build() {
            ExamCompletedEvent o = new ExamCompletedEvent();
            o.sessionId = this.sessionId;
            o.userId = this.userId;
            o.examId = this.examId;
            o.certificationId = this.certificationId;
            o.mode = this.mode;
            o.examTitle = this.examTitle;
            o.totalQuestions = this.totalQuestions;
            o.durationMinutes = this.durationMinutes;
            o.passingScore = this.passingScore;
            o.startTime = this.startTime;
            o.endTime = this.endTime;
            o.answeredCount = this.answeredCount;
            o.correctCount = this.correctCount;
            o.wrongCount = this.wrongCount;
            o.flaggedCount = this.flaggedCount;
            o.timeSpentSeconds = this.timeSpentSeconds;
            o.answers = this.answers;
            o.eventId = this.eventId;
            o.eventVersion = this.eventVersion;
            o.eventTime = this.eventTime;
            return o;
        }
    }

    public String getEventId() { return eventId; }
    public void setEventId(String eventId) { this.eventId = eventId; }

    public Integer getEventVersion() { return eventVersion; }
    public void setEventVersion(Integer eventVersion) { this.eventVersion = eventVersion; }

    public Long getSessionId() { return sessionId; }
    public void setSessionId(Long sessionId) { this.sessionId = sessionId; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public Long getExamId() { return examId; }
    public void setExamId(Long examId) { this.examId = examId; }

    public Long getCertificationId() { return certificationId; }
    public void setCertificationId(Long certificationId) { this.certificationId = certificationId; }

    public String getMode() { return mode; }
    public void setMode(String mode) { this.mode = mode; }

    public String getExamTitle() { return examTitle; }
    public void setExamTitle(String examTitle) { this.examTitle = examTitle; }

    public Integer getTotalQuestions() { return totalQuestions; }
    public void setTotalQuestions(Integer totalQuestions) { this.totalQuestions = totalQuestions; }

    public Integer getDurationMinutes() { return durationMinutes; }
    public void setDurationMinutes(Integer durationMinutes) { this.durationMinutes = durationMinutes; }

    public Integer getPassingScore() { return passingScore; }
    public void setPassingScore(Integer passingScore) { this.passingScore = passingScore; }

    public LocalDateTime getStartTime() { return startTime; }
    public void setStartTime(LocalDateTime startTime) { this.startTime = startTime; }

    public LocalDateTime getEndTime() { return endTime; }
    public void setEndTime(LocalDateTime endTime) { this.endTime = endTime; }

    public Integer getAnsweredCount() { return answeredCount; }
    public void setAnsweredCount(Integer answeredCount) { this.answeredCount = answeredCount; }

    public Integer getCorrectCount() { return correctCount; }
    public void setCorrectCount(Integer correctCount) { this.correctCount = correctCount; }

    public Integer getWrongCount() { return wrongCount; }
    public void setWrongCount(Integer wrongCount) { this.wrongCount = wrongCount; }

    public Integer getFlaggedCount() { return flaggedCount; }
    public void setFlaggedCount(Integer flaggedCount) { this.flaggedCount = flaggedCount; }

    public Integer getTimeSpentSeconds() { return timeSpentSeconds; }
    public void setTimeSpentSeconds(Integer timeSpentSeconds) { this.timeSpentSeconds = timeSpentSeconds; }

    public List<UserAnswerData> getAnswers() { return answers; }
    public void setAnswers(List<UserAnswerData> answers) { this.answers = answers; }

    public LocalDateTime getEventTime() { return eventTime; }
    public void setEventTime(LocalDateTime eventTime) { this.eventTime = eventTime; }

    /**
     * Nested class containing user answer data with question metadata.
     */
    public static class UserAnswerData implements Serializable {

        @Serial
        private static final long serialVersionUID = 1L;

        /**
         * Question ID.
         */
        private Long questionId;

        /**
         * Topic ID for performance tracking.
         */
        private Long topicId;

        /**
         * Topic name.
         */
        private String topicName;

        /**
         * Selected option IDs by the user.
         */
        private Long[] selectedOptionIds;

        /**
         * Correct option IDs for the question.
         */
        private Long[] correctOptionIds;

        /**
         * Whether the answer is correct.
         */
        private Boolean isCorrect;

        /**
         * Whether the question was flagged.
         */
        private Boolean isFlagged;

        /**
         * Time spent on this question in seconds.
         */
        private Integer timeSpentSeconds;

        /**
         * When the answer was submitted.
         */
        private LocalDateTime answeredAt;

        /**
         * Question text for result display.
         */
        private String questionText;

        /**
         * Difficulty level of the question.
         */
        private String difficulty;

        /**
         * Explanation for the correct answer.
         */
        private String explanation;

        /**
         * Reference material for further study.
         */
        private String reference;

        public UserAnswerData() {
        }

        public UserAnswerData(Long questionId, Long topicId, String topicName,
                              Long[] selectedOptionIds, Long[] correctOptionIds,
                              Boolean isCorrect, Boolean isFlagged, Integer timeSpentSeconds,
                              LocalDateTime answeredAt, String questionText,
                              String explanation, String reference) {
            this.questionId = questionId;
            this.topicId = topicId;
            this.topicName = topicName;
            this.selectedOptionIds = selectedOptionIds;
            this.correctOptionIds = correctOptionIds;
            this.isCorrect = isCorrect;
            this.isFlagged = isFlagged;
            this.timeSpentSeconds = timeSpentSeconds;
            this.answeredAt = answeredAt;
            this.questionText = questionText;
            this.explanation = explanation;
            this.reference = reference;
        }

        public static Builder builder() {
            return new Builder();
        }

        public static class Builder {
            private Long questionId;
            private Long topicId;
            private String topicName;
            private Long[] selectedOptionIds;
            private Long[] correctOptionIds;
            private Boolean isCorrect;
            private Boolean isFlagged;
            private Integer timeSpentSeconds;
            private LocalDateTime answeredAt;
            private String questionText;
            private String difficulty;
            private String explanation;
            private String reference;

            public Builder questionId(Long v) { this.questionId = v; return this; }
            public Builder topicId(Long v) { this.topicId = v; return this; }
            public Builder topicName(String v) { this.topicName = v; return this; }
            public Builder selectedOptionIds(Long[] v) { this.selectedOptionIds = v; return this; }
            public Builder correctOptionIds(Long[] v) { this.correctOptionIds = v; return this; }
            public Builder isCorrect(Boolean v) { this.isCorrect = v; return this; }
            public Builder isFlagged(Boolean v) { this.isFlagged = v; return this; }
            public Builder timeSpentSeconds(Integer v) { this.timeSpentSeconds = v; return this; }
            public Builder answeredAt(LocalDateTime v) { this.answeredAt = v; return this; }
            public Builder questionText(String v) { this.questionText = v; return this; }
            public Builder difficulty(String v) { this.difficulty = v; return this; }
            public Builder explanation(String v) { this.explanation = v; return this; }
            public Builder reference(String v) { this.reference = v; return this; }

            public UserAnswerData build() {
                UserAnswerData o = new UserAnswerData();
                o.questionId = this.questionId;
                o.topicId = this.topicId;
                o.topicName = this.topicName;
                o.selectedOptionIds = this.selectedOptionIds;
                o.correctOptionIds = this.correctOptionIds;
                o.isCorrect = this.isCorrect;
                o.isFlagged = this.isFlagged;
                o.timeSpentSeconds = this.timeSpentSeconds;
                o.answeredAt = this.answeredAt;
                o.questionText = this.questionText;
                o.difficulty = this.difficulty;
                o.explanation = this.explanation;
                o.reference = this.reference;
                return o;
            }
        }

        public Long getQuestionId() { return questionId; }
        public void setQuestionId(Long questionId) { this.questionId = questionId; }

        public Long getTopicId() { return topicId; }
        public void setTopicId(Long topicId) { this.topicId = topicId; }

        public String getTopicName() { return topicName; }
        public void setTopicName(String topicName) { this.topicName = topicName; }

        public Long[] getSelectedOptionIds() { return selectedOptionIds; }
        public void setSelectedOptionIds(Long[] selectedOptionIds) { this.selectedOptionIds = selectedOptionIds; }

        public Long[] getCorrectOptionIds() { return correctOptionIds; }
        public void setCorrectOptionIds(Long[] correctOptionIds) { this.correctOptionIds = correctOptionIds; }

        public Boolean getIsCorrect() { return isCorrect; }
        public void setIsCorrect(Boolean isCorrect) { this.isCorrect = isCorrect; }

        public Boolean getIsFlagged() { return isFlagged; }
        public void setIsFlagged(Boolean isFlagged) { this.isFlagged = isFlagged; }

        public Integer getTimeSpentSeconds() { return timeSpentSeconds; }
        public void setTimeSpentSeconds(Integer timeSpentSeconds) { this.timeSpentSeconds = timeSpentSeconds; }

        public LocalDateTime getAnsweredAt() { return answeredAt; }
        public void setAnsweredAt(LocalDateTime answeredAt) { this.answeredAt = answeredAt; }

        public String getQuestionText() { return questionText; }
        public void setQuestionText(String questionText) { this.questionText = questionText; }

        public String getDifficulty() { return difficulty; }
        public void setDifficulty(String difficulty) { this.difficulty = difficulty; }

        public String getExplanation() { return explanation; }
        public void setExplanation(String explanation) { this.explanation = explanation; }

        public String getReference() { return reference; }
        public void setReference(String reference) { this.reference = reference; }
    }
}

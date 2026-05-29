package com.certimaster.learning.domain.entity;

import com.certimaster.shared.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

/**
 * Entity representing the result of a single question in an exam.
 */
@Entity
@Table(name = "question_results")
public class QuestionResult extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "result_id")
    private ExamResult examResult;

    @Column(name = "question_id", nullable = false)
    private Long questionId;

    @Column(name = "topic_id")
    private Long topicId;

    @Column(name = "topic_name")
    private String topicName;

    @Column(name = "difficulty", length = 20)
    private String difficulty;

    @Column(name = "question_type", length = 50)
    private String questionType;

    @Column(name = "question_content", columnDefinition = "TEXT")
    private String questionContent;

    @Column(name = "user_answer_ids", columnDefinition = "bigint[]")
    @JdbcTypeCode(SqlTypes.ARRAY)
    private Long[] userAnswerIds;

    @Column(name = "correct_answer_ids", columnDefinition = "bigint[]")
    @JdbcTypeCode(SqlTypes.ARRAY)
    private Long[] correctAnswerIds;

    @Column(name = "is_correct")
    private Boolean isCorrect;

    @Column(name = "time_spent_seconds")
    private Integer timeSpentSeconds;

    @Column(name = "explanation", columnDefinition = "TEXT")
    private String explanation;

    @Column(name = "reference_links", columnDefinition = "TEXT")
    private String referenceLinks;

    @Column(name = "was_flagged")
    private Boolean wasFlagged = false;

    @Column(name = "was_reviewed")
    private Boolean wasReviewed = false;

    public QuestionResult() {
        super();
    }

    public QuestionResult(ExamResult examResult, Long questionId, Long topicId, String topicName,
                           String difficulty, String questionType, String questionContent,
                           Long[] userAnswerIds, Long[] correctAnswerIds, Boolean isCorrect,
                           Integer timeSpentSeconds, String explanation, String referenceLinks,
                           Boolean wasFlagged, Boolean wasReviewed) {
        super();
        this.examResult = examResult;
        this.questionId = questionId;
        this.topicId = topicId;
        this.topicName = topicName;
        this.difficulty = difficulty;
        this.questionType = questionType;
        this.questionContent = questionContent;
        this.userAnswerIds = userAnswerIds;
        this.correctAnswerIds = correctAnswerIds;
        this.isCorrect = isCorrect;
        this.timeSpentSeconds = timeSpentSeconds;
        this.explanation = explanation;
        this.referenceLinks = referenceLinks;
        this.wasFlagged = wasFlagged;
        this.wasReviewed = wasReviewed;
    }

    public ExamResult getExamResult() { return examResult; }
    public void setExamResult(ExamResult examResult) { this.examResult = examResult; }

    public Long getQuestionId() { return questionId; }
    public void setQuestionId(Long questionId) { this.questionId = questionId; }

    public Long getTopicId() { return topicId; }
    public void setTopicId(Long topicId) { this.topicId = topicId; }

    public String getTopicName() { return topicName; }
    public void setTopicName(String topicName) { this.topicName = topicName; }

    public String getDifficulty() { return difficulty; }
    public void setDifficulty(String difficulty) { this.difficulty = difficulty; }

    public String getQuestionType() { return questionType; }
    public void setQuestionType(String questionType) { this.questionType = questionType; }

    public String getQuestionContent() { return questionContent; }
    public void setQuestionContent(String questionContent) { this.questionContent = questionContent; }

    public Long[] getUserAnswerIds() { return userAnswerIds; }
    public void setUserAnswerIds(Long[] userAnswerIds) { this.userAnswerIds = userAnswerIds; }

    public Long[] getCorrectAnswerIds() { return correctAnswerIds; }
    public void setCorrectAnswerIds(Long[] correctAnswerIds) { this.correctAnswerIds = correctAnswerIds; }

    public Boolean getIsCorrect() { return isCorrect; }
    public void setIsCorrect(Boolean isCorrect) { this.isCorrect = isCorrect; }

    public Integer getTimeSpentSeconds() { return timeSpentSeconds; }
    public void setTimeSpentSeconds(Integer timeSpentSeconds) { this.timeSpentSeconds = timeSpentSeconds; }

    public String getExplanation() { return explanation; }
    public void setExplanation(String explanation) { this.explanation = explanation; }

    public String getReferenceLinks() { return referenceLinks; }
    public void setReferenceLinks(String referenceLinks) { this.referenceLinks = referenceLinks; }

    public Boolean getWasFlagged() { return wasFlagged; }
    public void setWasFlagged(Boolean wasFlagged) { this.wasFlagged = wasFlagged; }

    public Boolean getWasReviewed() { return wasReviewed; }
    public void setWasReviewed(Boolean wasReviewed) { this.wasReviewed = wasReviewed; }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private ExamResult examResult;
        private Long questionId;
        private Long topicId;
        private String topicName;
        private String difficulty;
        private String questionType;
        private String questionContent;
        private Long[] userAnswerIds;
        private Long[] correctAnswerIds;
        private Boolean isCorrect;
        private Integer timeSpentSeconds;
        private String explanation;
        private String referenceLinks;
        private Boolean wasFlagged = false;
        private Boolean wasReviewed = false;

        public Builder examResult(ExamResult v) { this.examResult = v; return this; }
        public Builder questionId(Long v) { this.questionId = v; return this; }
        public Builder topicId(Long v) { this.topicId = v; return this; }
        public Builder topicName(String v) { this.topicName = v; return this; }
        public Builder difficulty(String v) { this.difficulty = v; return this; }
        public Builder questionType(String v) { this.questionType = v; return this; }
        public Builder questionContent(String v) { this.questionContent = v; return this; }
        public Builder userAnswerIds(Long[] v) { this.userAnswerIds = v; return this; }
        public Builder correctAnswerIds(Long[] v) { this.correctAnswerIds = v; return this; }
        public Builder isCorrect(Boolean v) { this.isCorrect = v; return this; }
        public Builder timeSpentSeconds(Integer v) { this.timeSpentSeconds = v; return this; }
        public Builder explanation(String v) { this.explanation = v; return this; }
        public Builder referenceLinks(String v) { this.referenceLinks = v; return this; }
        public Builder wasFlagged(Boolean v) { this.wasFlagged = v; return this; }
        public Builder wasReviewed(Boolean v) { this.wasReviewed = v; return this; }

        public QuestionResult build() {
            QuestionResult o = new QuestionResult();
            o.examResult = this.examResult;
            o.questionId = this.questionId;
            o.topicId = this.topicId;
            o.topicName = this.topicName;
            o.difficulty = this.difficulty;
            o.questionType = this.questionType;
            o.questionContent = this.questionContent;
            o.userAnswerIds = this.userAnswerIds;
            o.correctAnswerIds = this.correctAnswerIds;
            o.isCorrect = this.isCorrect;
            o.timeSpentSeconds = this.timeSpentSeconds;
            o.explanation = this.explanation;
            o.referenceLinks = this.referenceLinks;
            o.wasFlagged = this.wasFlagged;
            o.wasReviewed = this.wasReviewed;
            return o;
        }
    }
}

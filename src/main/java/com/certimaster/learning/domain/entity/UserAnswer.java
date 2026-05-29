package com.certimaster.learning.domain.entity;

import com.certimaster.shared.entity.BaseEntity;
import com.certimaster.learning.domain.entity.Question;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.Map;

@Entity
@Table(name = "user_answers")
public class UserAnswer extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "session_id", nullable = false)
    private UserExamSession userExamSession;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "question_id", nullable = false)
    private Question question;

    @Column(name = "selected_option_ids", columnDefinition = "bigint[]")
    @JdbcTypeCode(SqlTypes.ARRAY)
    private Long[] selectedOptionIds;

    @Column(name = "correct_option_ids", columnDefinition = "bigint[]")
    @JdbcTypeCode(SqlTypes.ARRAY)
    private Long[] correctOptionIds;

    @Column(name = "is_correct")
    private Boolean isCorrect;

    @Column(name = "is_flagged")
    private Boolean isFlagged = false;

    @Column(name = "time_spent_seconds")
    private Integer timeSpentSeconds = 0;

    @Column(name = "answered_at")
    private LocalDateTime answeredAt;

    @Column(name = "question_order")
    private Integer questionOrder;

    @Column(name = "option_order_map", columnDefinition = "jsonb")
    @JdbcTypeCode(SqlTypes.JSON)
    private Map<Long, Integer> optionOrderMap;

    public UserAnswer() {
        super();
    }

    public UserAnswer(UserExamSession userExamSession, Question question,
                      Long[] selectedOptionIds, Long[] correctOptionIds,
                      Boolean isCorrect, Boolean isFlagged, Integer timeSpentSeconds,
                      LocalDateTime answeredAt) {
        super();
        this.userExamSession = userExamSession;
        this.question = question;
        this.selectedOptionIds = selectedOptionIds;
        this.correctOptionIds = correctOptionIds;
        this.isCorrect = isCorrect;
        this.isFlagged = isFlagged;
        this.timeSpentSeconds = timeSpentSeconds;
        this.answeredAt = answeredAt;
    }

    public UserExamSession getUserExamSession() { return userExamSession; }
    public void setUserExamSession(UserExamSession userExamSession) { this.userExamSession = userExamSession; }

    public Question getQuestion() { return question; }
    public void setQuestion(Question question) { this.question = question; }

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

    public Integer getQuestionOrder() { return questionOrder; }
    public void setQuestionOrder(Integer questionOrder) { this.questionOrder = questionOrder; }

    public Map<Long, Integer> getOptionOrderMap() { return optionOrderMap; }
    public void setOptionOrderMap(Map<Long, Integer> optionOrderMap) { this.optionOrderMap = optionOrderMap; }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private UserExamSession userExamSession;
        private Question question;
        private Long[] selectedOptionIds;
        private Long[] correctOptionIds;
        private Boolean isCorrect;
        private Boolean isFlagged = false;
        private Integer timeSpentSeconds = 0;
        private LocalDateTime answeredAt;
        private Integer questionOrder;
        private Map<Long, Integer> optionOrderMap;

        public Builder userExamSession(UserExamSession v) { this.userExamSession = v; return this; }
        public Builder question(Question v) { this.question = v; return this; }
        public Builder selectedOptionIds(Long[] v) { this.selectedOptionIds = v; return this; }
        public Builder correctOptionIds(Long[] v) { this.correctOptionIds = v; return this; }
        public Builder isCorrect(Boolean v) { this.isCorrect = v; return this; }
        public Builder isFlagged(Boolean v) { this.isFlagged = v; return this; }
        public Builder timeSpentSeconds(Integer v) { this.timeSpentSeconds = v; return this; }
        public Builder answeredAt(LocalDateTime v) { this.answeredAt = v; return this; }
        public Builder questionOrder(Integer v) { this.questionOrder = v; return this; }
        public Builder optionOrderMap(Map<Long, Integer> v) { this.optionOrderMap = v; return this; }

        public UserAnswer build() {
            UserAnswer o = new UserAnswer();
            o.userExamSession = this.userExamSession;
            o.question = this.question;
            o.selectedOptionIds = this.selectedOptionIds;
            o.correctOptionIds = this.correctOptionIds;
            o.isCorrect = this.isCorrect;
            o.isFlagged = this.isFlagged;
            o.timeSpentSeconds = this.timeSpentSeconds;
            o.answeredAt = this.answeredAt;
            o.questionOrder = this.questionOrder;
            o.optionOrderMap = this.optionOrderMap;
            return o;
        }
    }
}

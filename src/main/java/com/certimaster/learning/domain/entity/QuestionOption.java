package com.certimaster.learning.domain.entity;

import com.certimaster.shared.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "question_options")
public class QuestionOption extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "question_id", nullable = false)
    private Question question;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String content;

    @Column(name = "is_correct")
    private Boolean isCorrect;

    @Column(name = "order_index")
    private Integer orderIndex;

    public QuestionOption() {
        super();
    }

    public QuestionOption(Question question, String content, Boolean isCorrect, Integer orderIndex) {
        super();
        this.question = question;
        this.content = content;
        this.isCorrect = isCorrect;
        this.orderIndex = orderIndex;
    }

    public Question getQuestion() { return question; }
    public void setQuestion(Question question) { this.question = question; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public Boolean getIsCorrect() { return isCorrect; }
    public void setIsCorrect(Boolean isCorrect) { this.isCorrect = isCorrect; }

    public Integer getOrderIndex() { return orderIndex; }
    public void setOrderIndex(Integer orderIndex) { this.orderIndex = orderIndex; }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private Question question;
        private String content;
        private Boolean isCorrect;
        private Integer orderIndex;

        public Builder question(Question v) { this.question = v; return this; }
        public Builder content(String v) { this.content = v; return this; }
        public Builder isCorrect(Boolean v) { this.isCorrect = v; return this; }
        public Builder orderIndex(Integer v) { this.orderIndex = v; return this; }

        public QuestionOption build() {
            QuestionOption o = new QuestionOption();
            o.question = this.question;
            o.content = this.content;
            o.isCorrect = this.isCorrect;
            o.orderIndex = this.orderIndex;
            return o;
        }
    }
}

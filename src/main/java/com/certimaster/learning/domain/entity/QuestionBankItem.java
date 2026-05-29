package com.certimaster.learning.domain.entity;

import com.certimaster.shared.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "question_bank_items")
public class QuestionBankItem extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "question_bank_id", nullable = false)
    private QuestionBank questionBank;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "question_id", nullable = false)
    private Question question;

    @Column(name = "position", nullable = false)
    private Integer position;

    public QuestionBankItem() {
        super();
    }

    public QuestionBank getQuestionBank() { return questionBank; }
    public void setQuestionBank(QuestionBank questionBank) { this.questionBank = questionBank; }

    public Question getQuestion() { return question; }
    public void setQuestion(Question question) { this.question = question; }

    public Integer getPosition() { return position; }
    public void setPosition(Integer position) { this.position = position; }
}

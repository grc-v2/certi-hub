package com.certimaster.learning.domain.entity;

import com.certimaster.shared.entity.BaseEntity;
import com.certimaster.learning.domain.entity.Question;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "exam_questions")
public class ExamQuestion extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "exam_id", nullable = false)
    private Exam exam;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "question_id", nullable = false)
    private Question question;

    @Column(name = "order_index")
    private Integer orderIndex;

    public ExamQuestion() {
        super();
    }

    public ExamQuestion(Exam exam, Question question, Integer orderIndex) {
        super();
        this.exam = exam;
        this.question = question;
        this.orderIndex = orderIndex;
    }

    public Exam getExam() { return exam; }
    public void setExam(Exam exam) { this.exam = exam; }

    public Question getQuestion() { return question; }
    public void setQuestion(Question question) { this.question = question; }

    public Integer getOrderIndex() { return orderIndex; }
    public void setOrderIndex(Integer orderIndex) { this.orderIndex = orderIndex; }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private Exam exam;
        private Question question;
        private Integer orderIndex;

        public Builder exam(Exam v) { this.exam = v; return this; }
        public Builder question(Question v) { this.question = v; return this; }
        public Builder orderIndex(Integer v) { this.orderIndex = v; return this; }

        public ExamQuestion build() {
            ExamQuestion o = new ExamQuestion();
            o.exam = this.exam;
            o.question = this.question;
            o.orderIndex = this.orderIndex;
            return o;
        }
    }
}

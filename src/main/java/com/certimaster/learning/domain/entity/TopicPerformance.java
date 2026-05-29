package com.certimaster.learning.domain.entity;

import com.certimaster.shared.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import java.math.BigDecimal;

@Entity
@Table(name = "topic_performances")
public class TopicPerformance extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "exam_result_id", nullable = false)
    private ExamResult examResult;

    @Column(name = "topic_id", nullable = false)
    private Long topicId;

    @Column(name = "topic_name", nullable = false, length = 255)
    private String topicName;

    @Column(name = "weight", precision = 5, scale = 2)
    private BigDecimal weight;

    @Column(name = "total_questions", nullable = false)
    private Integer totalQuestions;

    @Column(name = "correct_answers", nullable = false)
    private Integer correctAnswers;

    @Column(name = "wrong_answers", nullable = false)
    private Integer wrongAnswers;

    @Column(name = "percentage", precision = 5, scale = 2)
    private BigDecimal percentage;

    public TopicPerformance() {
        super();
    }

    public ExamResult getExamResult() { return examResult; }
    public void setExamResult(ExamResult examResult) { this.examResult = examResult; }

    public Long getTopicId() { return topicId; }
    public void setTopicId(Long topicId) { this.topicId = topicId; }

    public String getTopicName() { return topicName; }
    public void setTopicName(String topicName) { this.topicName = topicName; }

    public BigDecimal getWeight() { return weight; }
    public void setWeight(BigDecimal weight) { this.weight = weight; }

    public Integer getTotalQuestions() { return totalQuestions; }
    public void setTotalQuestions(Integer totalQuestions) { this.totalQuestions = totalQuestions; }

    public Integer getCorrectAnswers() { return correctAnswers; }
    public void setCorrectAnswers(Integer correctAnswers) { this.correctAnswers = correctAnswers; }

    public Integer getWrongAnswers() { return wrongAnswers; }
    public void setWrongAnswers(Integer wrongAnswers) { this.wrongAnswers = wrongAnswers; }

    public BigDecimal getPercentage() { return percentage; }
    public void setPercentage(BigDecimal percentage) { this.percentage = percentage; }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private ExamResult examResult;
        private Long topicId;
        private String topicName;
        private BigDecimal weight;
        private Integer totalQuestions;
        private Integer correctAnswers;
        private Integer wrongAnswers;
        private BigDecimal percentage;

        public Builder examResult(ExamResult v) { this.examResult = v; return this; }
        public Builder topicId(Long v) { this.topicId = v; return this; }
        public Builder topicName(String v) { this.topicName = v; return this; }
        public Builder weight(BigDecimal v) { this.weight = v; return this; }
        public Builder totalQuestions(Integer v) { this.totalQuestions = v; return this; }
        public Builder correctAnswers(Integer v) { this.correctAnswers = v; return this; }
        public Builder wrongAnswers(Integer v) { this.wrongAnswers = v; return this; }
        public Builder percentage(BigDecimal v) { this.percentage = v; return this; }

        public TopicPerformance build() {
            TopicPerformance o = new TopicPerformance();
            o.examResult = this.examResult;
            o.topicId = this.topicId;
            o.topicName = this.topicName;
            o.weight = this.weight;
            o.totalQuestions = this.totalQuestions;
            o.correctAnswers = this.correctAnswers;
            o.wrongAnswers = this.wrongAnswers != null ? this.wrongAnswers :
                    (this.totalQuestions != null && this.correctAnswers != null
                            ? this.totalQuestions - this.correctAnswers : 0);
            o.percentage = this.percentage;
            return o;
        }
    }
}

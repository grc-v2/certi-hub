package com.certimaster.learning.domain.entity;

import com.certimaster.shared.entity.BaseEntity;
import com.certimaster.learning.domain.entity.Tag;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "question_tags")
public class QuestionTag extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "question_id", nullable = false)
    private Question question;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tag_id", nullable = false)
    private Tag tag;

    public QuestionTag() {
        super();
    }

    public QuestionTag(Question question, Tag tag) {
        super();
        this.question = question;
        this.tag = tag;
    }

    public Question getQuestion() { return question; }
    public void setQuestion(Question question) { this.question = question; }

    public Tag getTag() { return tag; }
    public void setTag(Tag tag) { this.tag = tag; }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private Question question;
        private Tag tag;

        public Builder question(Question v) { this.question = v; return this; }
        public Builder tag(Tag v) { this.tag = v; return this; }

        public QuestionTag build() {
            QuestionTag o = new QuestionTag();
            o.question = this.question;
            o.tag = this.tag;
            return o;
        }
    }
}

package com.certimaster.learning.adapter.web.response;

import java.util.List;

public class AnswerFeedbackResponse {

    private Long questionId;
    private Boolean answered;
    private Boolean isCorrect;
    private List<Long> correctOptionIds;
    private String explanation;
    private List<String> referenceLinks;

    public AnswerFeedbackResponse() {
    }

    public AnswerFeedbackResponse(Long questionId, Boolean answered, Boolean isCorrect,
                                   List<Long> correctOptionIds, String explanation,
                                   List<String> referenceLinks) {
        this.questionId = questionId;
        this.answered = answered;
        this.isCorrect = isCorrect;
        this.correctOptionIds = correctOptionIds;
        this.explanation = explanation;
        this.referenceLinks = referenceLinks;
    }

    public Long getQuestionId() {
        return questionId;
    }

    public void setQuestionId(Long questionId) {
        this.questionId = questionId;
    }

    public Boolean getAnswered() {
        return answered;
    }

    public void setAnswered(Boolean answered) {
        this.answered = answered;
    }

    public Boolean getIsCorrect() {
        return isCorrect;
    }

    public void setIsCorrect(Boolean isCorrect) {
        this.isCorrect = isCorrect;
    }

    public List<Long> getCorrectOptionIds() {
        return correctOptionIds;
    }

    public void setCorrectOptionIds(List<Long> correctOptionIds) {
        this.correctOptionIds = correctOptionIds;
    }

    public String getExplanation() {
        return explanation;
    }

    public void setExplanation(String explanation) {
        this.explanation = explanation;
    }

    public List<String> getReferenceLinks() {
        return referenceLinks;
    }

    public void setReferenceLinks(List<String> referenceLinks) {
        this.referenceLinks = referenceLinks;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Long questionId;
        private Boolean answered;
        private Boolean isCorrect;
        private List<Long> correctOptionIds;
        private String explanation;
        private List<String> referenceLinks;

        public Builder questionId(Long v) { this.questionId = v; return this; }
        public Builder answered(Boolean v) { this.answered = v; return this; }
        public Builder isCorrect(Boolean v) { this.isCorrect = v; return this; }
        public Builder correctOptionIds(List<Long> v) { this.correctOptionIds = v; return this; }
        public Builder explanation(String v) { this.explanation = v; return this; }
        public Builder referenceLinks(List<String> v) { this.referenceLinks = v; return this; }

        public AnswerFeedbackResponse build() {
            AnswerFeedbackResponse o = new AnswerFeedbackResponse();
            o.questionId = this.questionId;
            o.answered = this.answered;
            o.isCorrect = this.isCorrect;
            o.correctOptionIds = this.correctOptionIds;
            o.explanation = this.explanation;
            o.referenceLinks = this.referenceLinks;
            return o;
        }
    }
}

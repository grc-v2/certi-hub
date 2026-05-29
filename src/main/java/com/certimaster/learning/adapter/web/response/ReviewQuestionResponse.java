package com.certimaster.learning.adapter.web.response;

import com.certimaster.learning.adapter.web.response.QuestionResponse;

import java.util.List;

public class ReviewQuestionResponse {

    private QuestionResponse question;
    private List<Long> selectedOptionIds;
    private List<Long> correctOptionIds;
    private Boolean isCorrect;
    private String explanation;
    private List<String> referenceLinks;

    public ReviewQuestionResponse() {
    }

    public QuestionResponse getQuestion() { return question; }
    public void setQuestion(QuestionResponse question) { this.question = question; }

    public List<Long> getSelectedOptionIds() { return selectedOptionIds; }
    public void setSelectedOptionIds(List<Long> selectedOptionIds) { this.selectedOptionIds = selectedOptionIds; }

    public List<Long> getCorrectOptionIds() { return correctOptionIds; }
    public void setCorrectOptionIds(List<Long> correctOptionIds) { this.correctOptionIds = correctOptionIds; }

    public Boolean getIsCorrect() { return isCorrect; }
    public void setIsCorrect(Boolean isCorrect) { this.isCorrect = isCorrect; }

    public String getExplanation() { return explanation; }
    public void setExplanation(String explanation) { this.explanation = explanation; }

    public List<String> getReferenceLinks() { return referenceLinks; }
    public void setReferenceLinks(List<String> referenceLinks) { this.referenceLinks = referenceLinks; }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private QuestionResponse question;
        private List<Long> selectedOptionIds;
        private List<Long> correctOptionIds;
        private Boolean isCorrect;
        private String explanation;
        private List<String> referenceLinks;

        public Builder question(QuestionResponse v) { this.question = v; return this; }
        public Builder selectedOptionIds(List<Long> v) { this.selectedOptionIds = v; return this; }
        public Builder correctOptionIds(List<Long> v) { this.correctOptionIds = v; return this; }
        public Builder isCorrect(Boolean v) { this.isCorrect = v; return this; }
        public Builder explanation(String v) { this.explanation = v; return this; }
        public Builder referenceLinks(List<String> v) { this.referenceLinks = v; return this; }

        public ReviewQuestionResponse build() {
            ReviewQuestionResponse o = new ReviewQuestionResponse();
            o.question = this.question;
            o.selectedOptionIds = this.selectedOptionIds;
            o.correctOptionIds = this.correctOptionIds;
            o.isCorrect = this.isCorrect;
            o.explanation = this.explanation;
            o.referenceLinks = this.referenceLinks;
            return o;
        }
    }
}

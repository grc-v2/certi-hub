package com.certimaster.learning.adapter.web.response;

import com.certimaster.learning.adapter.web.response.QuestionResponse;

import java.util.List;

public class QuestionWithAnswerResponse {

    private QuestionResponse question;
    private List<Long> selectedOptionIds;
    private Boolean isFlagged;
    private Boolean isCorrect;

    public QuestionWithAnswerResponse() {
    }

    public QuestionWithAnswerResponse(QuestionResponse question, List<Long> selectedOptionIds,
                                      Boolean isFlagged, Boolean isCorrect) {
        this.question = question;
        this.selectedOptionIds = selectedOptionIds;
        this.isFlagged = isFlagged;
        this.isCorrect = isCorrect;
    }

    public QuestionResponse getQuestion() {
        return question;
    }

    public void setQuestion(QuestionResponse question) {
        this.question = question;
    }

    public List<Long> getSelectedOptionIds() {
        return selectedOptionIds;
    }

    public void setSelectedOptionIds(List<Long> selectedOptionIds) {
        this.selectedOptionIds = selectedOptionIds;
    }

    public Boolean getIsFlagged() {
        return isFlagged;
    }

    public void setIsFlagged(Boolean isFlagged) {
        this.isFlagged = isFlagged;
    }

    public Boolean getIsCorrect() {
        return isCorrect;
    }

    public void setIsCorrect(Boolean isCorrect) {
        this.isCorrect = isCorrect;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private QuestionResponse question;
        private List<Long> selectedOptionIds;
        private Boolean isFlagged;
        private Boolean isCorrect;

        public Builder question(QuestionResponse v) { this.question = v; return this; }
        public Builder selectedOptionIds(List<Long> v) { this.selectedOptionIds = v; return this; }
        public Builder isFlagged(Boolean v) { this.isFlagged = v; return this; }
        public Builder isCorrect(Boolean v) { this.isCorrect = v; return this; }

        public QuestionWithAnswerResponse build() {
            QuestionWithAnswerResponse o = new QuestionWithAnswerResponse();
            o.question = this.question;
            o.selectedOptionIds = this.selectedOptionIds;
            o.isFlagged = this.isFlagged;
            o.isCorrect = this.isCorrect;
            return o;
        }
    }
}

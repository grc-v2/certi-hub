package com.certimaster.learning.adapter.web.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public class AnswerQuestionRequest {

    @NotNull(message = "Question ID is required")
    private Long questionId;

    @NotEmpty(message = "At least one option must be selected")
    private List<Long> selectedOptionIds;

    @Min(value = 0, message = "Time spent must be non-negative")
    private Integer timeSpentSeconds;

    private Boolean isFlagged;

    public AnswerQuestionRequest() {
    }

    public AnswerQuestionRequest(Long questionId, List<Long> selectedOptionIds, Integer timeSpentSeconds, Boolean isFlagged) {
        this.questionId = questionId;
        this.selectedOptionIds = selectedOptionIds;
        this.timeSpentSeconds = timeSpentSeconds;
        this.isFlagged = isFlagged;
    }

    public Long getQuestionId() {
        return questionId;
    }

    public void setQuestionId(Long questionId) {
        this.questionId = questionId;
    }

    public List<Long> getSelectedOptionIds() {
        return selectedOptionIds;
    }

    public void setSelectedOptionIds(List<Long> selectedOptionIds) {
        this.selectedOptionIds = selectedOptionIds;
    }

    public Integer getTimeSpentSeconds() {
        return timeSpentSeconds;
    }

    public void setTimeSpentSeconds(Integer timeSpentSeconds) {
        this.timeSpentSeconds = timeSpentSeconds;
    }

    public Boolean getIsFlagged() {
        return isFlagged;
    }

    public void setIsFlagged(Boolean isFlagged) {
        this.isFlagged = isFlagged;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Long questionId;
        private List<Long> selectedOptionIds;
        private Integer timeSpentSeconds;
        private Boolean isFlagged;

        public Builder questionId(Long v) { this.questionId = v; return this; }
        public Builder selectedOptionIds(List<Long> v) { this.selectedOptionIds = v; return this; }
        public Builder timeSpentSeconds(Integer v) { this.timeSpentSeconds = v; return this; }
        public Builder isFlagged(Boolean v) { this.isFlagged = v; return this; }

        public AnswerQuestionRequest build() {
            AnswerQuestionRequest o = new AnswerQuestionRequest();
            o.questionId = this.questionId;
            o.selectedOptionIds = this.selectedOptionIds;
            o.timeSpentSeconds = this.timeSpentSeconds;
            o.isFlagged = this.isFlagged;
            return o;
        }
    }
}

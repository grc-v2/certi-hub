package com.certimaster.learning.adapter.web.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class QuestionOptionRequest {

    @NotBlank(message = "Option content is required")
    @Size(max = 5000, message = "Content must not exceed 5000 characters")
    private String content;

    @NotNull(message = "isCorrect flag is required")
    private Boolean isCorrect;

    @NotNull(message = "Order index is required")
    @Min(value = 0, message = "Order index must be non-negative")
    private Integer orderIndex;

    public QuestionOptionRequest() {
    }

    public QuestionOptionRequest(String content, Boolean isCorrect, Integer orderIndex) {
        this.content = content;
        this.isCorrect = isCorrect;
        this.orderIndex = orderIndex;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Boolean getIsCorrect() {
        return isCorrect;
    }

    public void setIsCorrect(Boolean isCorrect) {
        this.isCorrect = isCorrect;
    }

    public Integer getOrderIndex() {
        return orderIndex;
    }

    public void setOrderIndex(Integer orderIndex) {
        this.orderIndex = orderIndex;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String content;
        private Boolean isCorrect;
        private Integer orderIndex;

        public Builder content(String v) { this.content = v; return this; }
        public Builder isCorrect(Boolean v) { this.isCorrect = v; return this; }
        public Builder orderIndex(Integer v) { this.orderIndex = v; return this; }

        public QuestionOptionRequest build() {
            QuestionOptionRequest o = new QuestionOptionRequest();
            o.content = this.content;
            o.isCorrect = this.isCorrect;
            o.orderIndex = this.orderIndex;
            return o;
        }
    }
}

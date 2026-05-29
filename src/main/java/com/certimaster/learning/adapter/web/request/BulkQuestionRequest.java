package com.certimaster.learning.adapter.web.request;

import java.util.List;

public class BulkQuestionRequest {

    private String questionText;
    private String difficulty;
    private String questionType;
    private String explanation;
    private List<QuestionOptionRequest> options;

    public BulkQuestionRequest() {}

    public String getQuestionText() { return questionText; }
    public void setQuestionText(String questionText) { this.questionText = questionText; }

    public String getDifficulty() { return difficulty; }
    public void setDifficulty(String difficulty) { this.difficulty = difficulty; }

    public String getQuestionType() { return questionType; }
    public void setQuestionType(String questionType) { this.questionType = questionType; }

    public String getExplanation() { return explanation; }
    public void setExplanation(String explanation) { this.explanation = explanation; }

    public List<QuestionOptionRequest> getOptions() { return options; }
    public void setOptions(List<QuestionOptionRequest> options) { this.options = options; }
}

package com.github.vitaliikapliuk.ask.persistance;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.github.vitaliikapliuk.ask.persistance.model._Question;

public class QuestionData extends Model {

    @JsonProperty(_Question.questionText)
    private String questionText;

    public String getQuestionText() {
        return questionText;
    }

    public void setQuestionText(String questionText) {
        this.questionText = questionText;
    }
}


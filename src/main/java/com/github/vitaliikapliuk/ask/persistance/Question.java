package com.github.vitaliikapliuk.ask.persistance;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.github.vitaliikapliuk.ask.persistance.model._Question;

public class Question extends QuestionData {

    @JsonProperty(_Question.countryIso)
    private String countryIso;

    public String getCountryIso() {
        return countryIso;
    }

    public void setCountryIso(String countryIso) {
        this.countryIso = countryIso;
    }
}

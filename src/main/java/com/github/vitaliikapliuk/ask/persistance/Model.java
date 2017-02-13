package com.github.vitaliikapliuk.ask.persistance;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class Model {

    @JsonIgnore
    private String id;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

}

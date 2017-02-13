package com.github.vitaliikapliuk.ask.persistance;

public enum ESIndex {

    ASK("ask");

    public final String index;

    ESIndex(String index) {
        this.index = index;
    }

    public enum AskType {
        QUESTION("question");

        public final String type;

        AskType(String type) {
            this.type = type;
        }
    }

}

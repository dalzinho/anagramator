package uk.co.mrdaly.anagramator.source;

import lombok.Getter;

public enum InputSource {

    WIKIPEDIA("https://en.wikipedia.org"),
    WORDLIST("https://en.wiktionary.org/wiki/");

    @Getter
    private final String uriBase;

    InputSource(String uriBase) {
        this.uriBase = uriBase;
    }
}

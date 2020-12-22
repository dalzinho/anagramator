package uk.co.mrdaly.anagramator.source;

import lombok.Getter;

import static uk.co.mrdaly.anagramator.service.WikiReaderService.WIKIBASE;
import static uk.co.mrdaly.anagramator.service.WordListIngestionService.WIKTIONARY_BASE;

public enum InputSource {
    WIKIPEDIA(WIKIBASE),
    WORDLIST(WIKTIONARY_BASE);

    @Getter
    private final String uriBase;

    InputSource(String uriBase) {
        this.uriBase = uriBase;
    }
}

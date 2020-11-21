package uk.co.mrdaly.anagramator.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import uk.co.mrdaly.anagramator.ingestor.WordListIngestor;
import uk.co.mrdaly.anagramator.service.LookupService;
import uk.co.mrdaly.anagramator.service.WordSorterService;

import java.io.IOException;
import java.util.List;

@RestController
public class Controller {

    private final WordListIngestor wordListIngestor;
    private final LookupService lookupService;

    public Controller(WordListIngestor wordListIngestor, LookupService lookupService) {
        this.wordListIngestor = wordListIngestor;
        this.lookupService = lookupService;
    }

    @GetMapping("/solve/{word}")
    public List<String> getAnagramsForWord(@PathVariable String word) {
            return lookupService.getAnagrams(word);
    }

    @GetMapping("/solve/{word}/{regex}")
    public List<String> getAnagramsForWordMatchingRegex(@PathVariable String word, @PathVariable String regex) {
        return lookupService.getAnagrams(word, regex);
    }

    @GetMapping("/ingest")
    public void triggerIngestion() throws IOException {
        wordListIngestor.ingestFromClasspath();
    }
}

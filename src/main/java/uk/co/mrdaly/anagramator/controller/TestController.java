package uk.co.mrdaly.anagramator.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import uk.co.mrdaly.anagramator.service.WikiReaderService;
import uk.co.mrdaly.anagramator.service.WordListIngestionService;

import java.io.IOException;

@RestController
public class TestController {

    WikiReaderService wikiReaderService;
    private final WordListIngestionService wordListIngestionService;


    public TestController(WikiReaderService wikiReaderService, WordListIngestionService wordListIngestionService) {
        this.wikiReaderService = wikiReaderService;
        this.wordListIngestionService = wordListIngestionService;
    }

    @GetMapping("/wiki/update-topics")
    public void updateTopics() throws IOException {
        wikiReaderService.updatePageSources();
    }

    @GetMapping("/ingest")
    public void triggerIngestion() throws IOException {
        wordListIngestionService.ingestFromClasspath("words_alpha.txt");
    }

}

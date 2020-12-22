package uk.co.mrdaly.anagramator.service;

import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;
import uk.co.mrdaly.anagramator.jpa.entity.SolverEntry;
import uk.co.mrdaly.anagramator.jpa.repository.SolverEntryRepository;
import uk.co.mrdaly.anagramator.source.InputSource;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Component
public class WordListIngestionService {


    private final WordSorterService wordSorterService;
    private final SolverEntryRepository solverEntryRepository;

    public WordListIngestionService(WordSorterService wordSorterService, SolverEntryRepository solverEntryRepository) {
        this.wordSorterService = wordSorterService;
        this.solverEntryRepository = solverEntryRepository;
    }

    public void ingestFromClasspath(String filename) throws IOException {
        ClassPathResource classPathResource = new ClassPathResource(filename);
        File file = classPathResource.getFile();

        List<SolverEntry> solverEntries = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            for (String line; (line = br.readLine()) != null; ) {
                if (solverEntries.size() == 1_000) {
                    solverEntryRepository.saveAll(solverEntries);
                    solverEntries = new ArrayList<>();
                }

                String sorted = wordSorterService.sortLetters(line);

                SolverEntry solverEntry = new SolverEntry();
                solverEntry.setText(line);
                solverEntry.setSortedText(sorted);
                solverEntry.setUri(InputSource.WORDLIST.getUriBase() + line);
                solverEntry.setInputSource(InputSource.WORDLIST);
                solverEntries.add(solverEntry);
            }
        }
        solverEntryRepository.saveAll(solverEntries);
    }
}

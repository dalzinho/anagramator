package uk.co.mrdaly.anagramator.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;
import uk.co.mrdaly.anagramator.dataloader.DataLoader;
import uk.co.mrdaly.anagramator.exception.AnagramatorException;
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
@Slf4j
public class WordListIngestionService extends DataLoader {

    public WordListIngestionService(WordSorterService wordSorterService, SolverEntryRepository solverEntryRepository, PrimeService primeService) {
        super(wordSorterService, solverEntryRepository, primeService);
    }

    public void ingestFromClasspath(String filename) throws IOException {
        ClassPathResource classPathResource = new ClassPathResource(filename);
        File file = classPathResource.getFile();

        List<SolverEntry> solverEntries = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            for (String line; (line = br.readLine()) != null; ) {
                if (solverEntries.size() == 10_000) {
                    solverEntryRepository.saveAll(solverEntries);
                    solverEntries = new ArrayList<>();
                }

                final SolverEntry solverEntry = buildEntry(line);
                solverEntries.add(solverEntry);
            }
        }
        solverEntryRepository.saveAll(solverEntries);
    }

    @Override
    protected SolverEntry buildEntry(String word) {

        try {
            SolverEntry solverEntry = new SolverEntry();
            solverEntry.setText(word);
            solverEntry.setTrimmedText(preprocessLookupFields(word));
            solverEntry.setUri(InputSource.WORDLIST.getUriBase() + word);
            solverEntry.setInputSource(InputSource.WORDLIST);
            solverEntry.setPrimeProduct(primeService.calculatePrimeSumForWord(solverEntry.getTrimmedText()));

        } catch (AnagramatorException e) {
            log.error("oh no", e);
        }
        return null;
    }
}

package uk.co.mrdaly.anagramator.dataloader;

import uk.co.mrdaly.anagramator.jpa.entity.SolverEntry;
import uk.co.mrdaly.anagramator.jpa.repository.SolverEntryRepository;
import uk.co.mrdaly.anagramator.service.PrimeService;
import uk.co.mrdaly.anagramator.service.WordSorterService;

import java.util.List;
import java.util.stream.Collectors;

public abstract class DataLoader {

    protected final WordSorterService wordSorterService;
    protected final SolverEntryRepository solverEntryRepository;
    protected final PrimeService primeService;

    protected DataLoader(WordSorterService wordSorterService, SolverEntryRepository solverEntryRepository, PrimeService primeService) {
        this.wordSorterService = wordSorterService;
        this.solverEntryRepository = solverEntryRepository;
        this.primeService = primeService;
    }

    protected String preprocessLookupFields(String s) {
        List<String> letters = s.chars()
                .mapToObj(i -> (char) i)
                .filter(Character::isAlphabetic)
                .map(String::valueOf)
                .map(String::toLowerCase)
                .map(String::trim)
                .collect(Collectors.toList());

        return String.join("", letters);
    }

    protected abstract SolverEntry buildEntry(String word);
}

package uk.co.mrdaly.anagramator.service;

import org.springframework.stereotype.Service;
import uk.co.mrdaly.anagramator.jpa.entity.SolverEntry;
import uk.co.mrdaly.anagramator.jpa.repository.SolverEntryRepository;
import uk.co.mrdaly.anagramator.model.SolutionReponse;
import uk.co.mrdaly.anagramator.source.InputSource;

import java.util.List;
import java.util.stream.Collectors;

import static uk.co.mrdaly.anagramator.service.WikiReaderService.WIKIBASE;

@Service
public class LookupService {

    private final WordSorterService wordSorterService;
    private final SolverEntryRepository solverEntryRepository;
    private final RegexMatchingService regexMatchingService;

    public LookupService(WordSorterService wordSorterService, SolverEntryRepository solverEntryRepository, RegexMatchingService regexMatchingService) {
        this.wordSorterService = wordSorterService;
        this.solverEntryRepository = solverEntryRepository;
        this.regexMatchingService = regexMatchingService;
    }

    public List<SolutionReponse> getAnagrams(String word) {
        return getAnagrams(word, word);
    }

    public List<SolutionReponse> getAnagrams(String word, String pattern) {
        String sorted = wordSorterService.sortLetters(word);

        return solverEntryRepository.findSolverEntryBySortedTextStartsWith(sorted)
                .stream()
                .filter(solverEntry -> regexMatchingService.apply(solverEntry.getTrimmedText(), pattern))
                .map(this::mapResponse)
                .collect(Collectors.toList());
    }

    public List<SolutionReponse> getMatching(String pattern) {
        return solverEntryRepository.findAll().stream()
                .filter(solverEntry -> regexMatchingService.apply(solverEntry.getTrimmedText(), pattern))
                .map(this::mapResponse)
                .collect(Collectors.toList());
    }

    private SolutionReponse mapResponse(SolverEntry solverEntry) {
        SolutionReponse solutionReponse = new SolutionReponse();
        solutionReponse.setText(solverEntry.getText());
        solutionReponse.setUri(solverEntry.getUri());
        return solutionReponse;
    }

}

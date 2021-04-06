package uk.co.mrdaly.anagramator.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import uk.co.mrdaly.anagramator.exception.AnagramatorException;
import uk.co.mrdaly.anagramator.jpa.entity.SolverEntry;
import uk.co.mrdaly.anagramator.jpa.repository.SolverEntryRepository;
import uk.co.mrdaly.anagramator.model.SolutionReponse;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class LookupService {

    private final SolverEntryRepository solverEntryRepository;
    private final RegexMatchingService regexMatchingService;
    private final PrimeService primeService;
    private final WordNormalisationService wordNormalisationService;

    public LookupService(SolverEntryRepository solverEntryRepository, RegexMatchingService regexMatchingService, PrimeService primeService, WordNormalisationService wordNormalisationService) {
        this.solverEntryRepository = solverEntryRepository;
        this.regexMatchingService = regexMatchingService;
        this.primeService = primeService;
        this.wordNormalisationService = wordNormalisationService;
    }

    public List<SolutionReponse> getAnagrams(String word) {
        List<SolutionReponse> solutionReponses = new ArrayList<>();
        try {
            final String normalised = wordNormalisationService.normalise(word);
            final BigInteger primeProduct = primeService.calculatePrimeSumForWord(normalised);

            solutionReponses = solverEntryRepository.findAllByPrimeProductEquals(primeProduct)
                    .stream()
                    .map(this::mapResponse)
                    .collect(Collectors.toList());
        } catch (AnagramatorException e) {
            log.error("{} couldn't be processed", word, e);
        }

        return solutionReponses;
    }

    public List<SolutionReponse> getAnagrams(String word, String pattern) {
        List<SolutionReponse> solutionReponses = new ArrayList<>();
        try {
            final String normalised = wordNormalisationService.normalise(word);
            final BigInteger primeProduct = primeService.calculatePrimeSumForWord(normalised);

            solutionReponses = solverEntryRepository.findAllByPrimeProductEquals(primeProduct)
                    .stream()
                    .filter(solverEntry -> regexMatchingService.apply(pattern, solverEntry.getTrimmedText()))
                    .map(this::mapResponse)
                    .collect(Collectors.toList());
        } catch (AnagramatorException e) {
            log.error("{} couldn't be processed", word, e);
        }

        return solutionReponses;
    }

    @Cacheable(value = "partialMatches", key = "#word")
    public List<SolutionReponse> anagramWithPartialMatches(String word) {
        List<SolutionReponse> solutionReponses = new ArrayList<>();
        try {
            final String normalised = wordNormalisationService.normalise(word);
            final BigInteger primeProduct = primeService.calculatePrimeSumForWord(normalised);
            solutionReponses = solverEntryRepository.findAllByPrimeProductGreaterThanEqual(primeProduct)
                    .stream()
                    .filter(solverEntry -> solverEntry.getPrimeProduct().mod(primeProduct).equals(0))
                    .sorted(Comparator.comparingInt(solverEntry -> solverEntry.getTrimmedText().length()))
                    .map(this::mapResponse)
                    .collect(Collectors.toList());
        } catch (AnagramatorException e) {
            log.error("{} couldn't be processed", word, e);
        }

        return solutionReponses;
    }

    public List<SolutionReponse> dumbAnagrams(String word) {
        List<SolutionReponse> solutionReponses = new ArrayList<>();
        try {
            final String normalised = wordNormalisationService.normalise(word);
            final BigInteger primeProduct = primeService.calculatePrimeSumForWord(normalised);
            solutionReponses = solverEntryRepository.findAllByPrimeProductEquals(primeProduct).stream().map(this::mapResponse).collect(Collectors.toList());
        } catch (AnagramatorException e) {
            log.error("{} couldn't be processed", word, e);
        }

        return solutionReponses;
    }

    @Cacheable(value = "patternMatches", key = "#pattern")
    public List<SolutionReponse> matchPattern(String pattern) {
        String sqlLikePattern = regexMatchingService.prepareSqlLike(pattern.toLowerCase());

        return solverEntryRepository.findAllByTrimmedTextLikeOrderByText(sqlLikePattern)
                .stream()
                .filter(solverEntry -> regexMatchingService.apply(pattern, solverEntry.getTrimmedText()))
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

package uk.co.mrdaly.anagramator.service;

import org.paukov.combinatorics3.Generator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import uk.co.mrdaly.anagramator.jpa.entity.SolverEntry;
import uk.co.mrdaly.anagramator.jpa.repository.SolverEntryRepository;
import uk.co.mrdaly.anagramator.model.SolutionReponse;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class LookupService {

    private final SolverEntryRepository solverEntryRepository;
    private final RegexMatchingService regexMatchingService;
    private final PrimeService primeService;
    private final WordNormalisationService wordNormalisationService;
    private final DataAccessService dataAccessService;

    @Value("${result.paging.size:20}")
    private int resultPagingSize;

    public LookupService(SolverEntryRepository solverEntryRepository, RegexMatchingService regexMatchingService, PrimeService primeService, WordNormalisationService wordNormalisationService, DataAccessService dataAccessService) {
        this.solverEntryRepository = solverEntryRepository;
        this.regexMatchingService = regexMatchingService;
        this.primeService = primeService;
        this.wordNormalisationService = wordNormalisationService;
        this.dataAccessService = dataAccessService;
    }

    public List<SolutionReponse> getAnagrams(String word) {
        return getAnagrams(word, "");
    }


    public List<SolutionReponse> getAnagrams(String word, String pattern) {
        String normalised = wordNormalisationService.normalise(word);

        final List<List<Long>> sumPrimePartitions = primeService.getAllPrimeSumPermutationsFromWord(normalised);
        final Map<Long, List<SolverEntry>> primeSumEntryMap = buildResultMapForUniquePrimeProducts(sumPrimePartitions);
        List<List<List<SolverEntry>>> partitionsMappedToSolverEntries = mapPrimeProductsToSolverEntries(sumPrimePartitions, primeSumEntryMap);
        List<List<List<SolverEntry>>> cartesianProducts = getCartesianProductsForPartitions(partitionsMappedToSolverEntries);

        return mapCartesianProductsToResponseObjects(cartesianProducts);
    }

    public List<SolutionReponse> slightlySmarterAnagram(String word) {

        final String normalised = wordNormalisationService.normalise(word);
        final long primeProduct = primeService.calculatePrimeSumForWord(normalised);
        return solverEntryRepository.findAllByPrimeProductGreaterThanEqual(primeProduct)
                .stream()
                .filter(solverEntry -> solverEntry.getPrimeProduct() % primeProduct == 0)
                .sorted(Comparator.comparingInt(solverEntry -> solverEntry.getTrimmedText().length()))
                .map(this::mapResponse)
                .collect(Collectors.toList());
    }

    public List<SolutionReponse> dumbAnagrams(String word) {
        final String normalised = wordNormalisationService.normalise(word);
        final long primeProduct = primeService.calculatePrimeSumForWord(normalised);
        return solverEntryRepository.findAllByPrimeProductEquals(primeProduct).stream().map(this::mapResponse).collect(Collectors.toList());
    }

    private List<SolutionReponse> mapCartesianProductsToResponseObjects(List<List<List<SolverEntry>>> cartesianProducts) {
        return cartesianProducts.stream()
                .map(this::mapCartestianProductsToString)
                .map(this::mapResponse)
                .collect(Collectors.toList());
    }


    private String mapCartestianProductsToString(List<List<SolverEntry>> cartesianProduct) {
        return cartesianProduct.stream().flatMap(Collection::stream).map(SolverEntry::getTrimmedText).collect(Collectors.joining(" "));
    }

    private List<List<List<SolverEntry>>> getCartesianProductsForPartitions(List<List<List<SolverEntry>>> partitionsMappedToSolverEntries) {
        List<List<List<SolverEntry>>> cartesianProducts = new ArrayList<>();
        for (List<List<SolverEntry>> entriesForPartition : partitionsMappedToSolverEntries) {
            final List<List<SolverEntry>> collect = Generator.cartesianProduct(entriesForPartition)
                    .stream()
                    .flatMap(Collection::stream)
                    .collect(Collectors.toList());
            cartesianProducts.add(collect);
        }
        return cartesianProducts;
    }

    private List<List<List<SolverEntry>>> mapPrimeProductsToSolverEntries(List<List<Long>> sumPrimePartitions, Map<Long, List<SolverEntry>> primeSumEntryMap) {
        List<List<List<SolverEntry>>> partionsMappedToSolverEntries = new ArrayList<>();

        for (List<Long> partions : sumPrimePartitions) {
            final List<List<SolverEntry>> mapped = partions
                    .stream()
                    .map(primeSumEntryMap::get)
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());
            partionsMappedToSolverEntries.add(mapped);
        }
        return partionsMappedToSolverEntries;
    }

    private Map<Long, List<SolverEntry>> buildResultMapForUniquePrimeProducts(List<List<Long>> sumPrimePartitions) {
        List<Long> uniquePrimes = sumPrimePartitions.stream().flatMap(Collection::stream).distinct().collect(Collectors.toList());

        return dataAccessService.findWordsByPrimeProductIn(uniquePrimes)
                .stream()
                .collect(Collectors.groupingBy(SolverEntry::getPrimeProduct));
    }

    public List<SolutionReponse> getMatching(String pattern) {
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

    private SolutionReponse mapResponse(String s) {
        SolutionReponse solutionReponse = new SolutionReponse();
        solutionReponse.setText(s);
        return solutionReponse;
    }
}

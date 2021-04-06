package uk.co.mrdaly.anagramator.service;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import uk.co.mrdaly.anagramator.exception.AnagramatorException;

import java.math.BigInteger;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
public class PrimeService {

    private static final Map<Character, Integer> PRIMES_MAP;
    private static final Set<Integer> PRIMES;

    static {
        PRIMES_MAP = new HashMap<>();
        PRIMES_MAP.put('a', 2);

        PRIMES = new HashSet<>(Collections.singletonList(2));

        for (int i = 1; i < 1000; i++) {
            int nextPrime = PRIMES.stream().max(Integer::compare).get() + 1;
            while (!isPrime(nextPrime)) {
                nextPrime++;
            }
            char c = 'a';
            c += i;
            PRIMES_MAP.put(c, nextPrime);
            PRIMES.add(nextPrime);
        }
    }

    private static boolean isPrime(int i) {
        return PRIMES.stream().noneMatch(prime -> i % prime == 0);
    }

    public BigInteger calculatePrimeSumForWord(String word) throws AnagramatorException {
            return mapWordLettersToPrimes(word)
                    .stream()
                    .map(BigInteger::valueOf)
                    .reduce(BigInteger::multiply)
                    .orElseThrow(() -> new AnagramatorException("can't calculate a prime sum for " + word));

    }

    @Cacheable("combinations")
    public List<List<Long>> getAllPrimeSumPermutationsFromWord(String word) {

        final List<Integer> wordAsListOfPrimes = mapWordLettersToPrimes(word);

        final List<List<List<Integer>>> partition = partition(wordAsListOfPrimes, 5);

        return partition
                .stream()
                .map(this::flattenPermutation)
                .distinct()
                .collect(Collectors.toList());

    }

    private List<Long> flattenPermutation(List<List<Integer>> permutation) {
        return permutation.stream()
                .filter(list -> !list.isEmpty())
                .map(this::sumSublist)
                .sorted()
                .collect(Collectors.toList());
    }

    private long sumSublist(List<Integer> sublist) {
        return sublist.stream()
                .mapToLong(Long::valueOf)
                .reduce((a, b) -> a * b)
                .getAsLong();
    }

    public List<List<List<Integer>>> partition(List<Integer> lst, int maxSublists) {
        final List<List<List<Integer>>> result = new ArrayList<>();
        // k = SUM ( pos of lst[i] * n^i )
        for (int k = 0; k < Math.pow(maxSublists, lst.size()); k++) {
            // initialize result
            List<List<Integer>> res = IntStream.range(0, maxSublists)
                    .mapToObj(i -> new ArrayList<Integer>())
                    .collect(Collectors.toList());
            // distribute elements to sub-lists
            int k2 = k;
            for (int i = 0; i < lst.size(); i++) {
                res.get(k2 % maxSublists).add(lst.get(i));
                k2 /= maxSublists;
            }
            result.add(res);
        }
        return result;
    }

    public List<Integer> mapWordLettersToPrimes(String word) {
        return word.chars()
                .map(c -> PRIMES_MAP.getOrDefault((char) c, 1)) /* todo the default here is sketchy */
                .boxed()
                .collect(Collectors.toList());
    }
}

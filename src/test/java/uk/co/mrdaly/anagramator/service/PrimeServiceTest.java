package uk.co.mrdaly.anagramator.service;

import org.junit.Before;
import org.junit.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

public class PrimeServiceTest {

    private PrimeService primeService;

    private static final String TEST_WORD = "abcd";
    @Before
    public void setUp() {
        primeService = new PrimeService();
        ReflectionTestUtils.setField(primeService, "maxWords", 5);
    }

    @Test
    public void calculatePrimeSumForWord() {

        final long actual = primeService.calculatePrimeSumForWord(TEST_WORD);
        assertEquals(2 * 3 * 5 * 7, actual);
    }

    @Test
    public void getAllCombinationsForSum() {
        final List<List<Long>> allPrimeSumsFromWord = primeService.getAllPrimeSumPermutationsFromWord(TEST_WORD);
        assertFalse(allPrimeSumsFromWord.isEmpty());
    }
}
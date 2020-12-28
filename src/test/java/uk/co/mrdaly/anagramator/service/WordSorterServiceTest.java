package uk.co.mrdaly.anagramator.service;


import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class WordSorterServiceTest {

    private WordSorterService wordSorterService = new WordSorterService();

    @Test
    public void sortsWord() {
        String abc = wordSorterService.sortLetters("cba");
        assertEquals("abc", abc);
    }


}
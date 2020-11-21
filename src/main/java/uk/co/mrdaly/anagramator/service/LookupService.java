package uk.co.mrdaly.anagramator.service;

import org.springframework.stereotype.Service;
import uk.co.mrdaly.anagramator.ingestor.WordListIngestor;

import java.util.List;
import java.util.regex.Pattern;

@Service
public class LookupService {

    private final WordSorterService wordSorterService;
    WordListIngestor wordListIngestor;

    public LookupService(WordSorterService wordSorterService, WordListIngestor wordListIngestor) {
        this.wordSorterService = wordSorterService;
        this.wordListIngestor = wordListIngestor;
    }

    public List<String> getAnagrams(String word) {
        String sorted = wordSorterService.sortLetters(word);
        return wordListIngestor.getAnagramsContaining(sorted);
    }

    public List<String> getAnagrams(String word, String pattern) {
        String sorted = wordSorterService.sortLetters(word);
        Pattern p = Pattern.compile(pattern);
        return wordListIngestor.getAnagramsContainingAndMatchingRegex(sorted, p);
    }
}

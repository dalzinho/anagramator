package uk.co.mrdaly.anagramator.ingestor;

import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;
import uk.co.mrdaly.anagramator.service.WordSorterService;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
public class WordListIngestor {

    private final WordSorterService wordSorterService;

    public WordListIngestor(WordSorterService wordSorterService) {
        this.wordSorterService = wordSorterService;
    }

    private final Map<String, List<String>> anagramSource = new HashMap<>();

    public void ingestFromClasspath() throws IOException {
        ClassPathResource classPathResource = new ClassPathResource("words_alpha.txt");
        File file = classPathResource.getFile();
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            for (String line; (line = br.readLine()) != null; ) {
                String sorted = wordSorterService.sortLetters(line);

                if (!anagramSource.containsKey(sorted)) {
                    List<String> anagrams = new ArrayList<>();
                    anagrams.add(line);
                    anagramSource.put(sorted, anagrams);
                } else {
                    List<String> anagrams = anagramSource.get(sorted);
                    anagrams.add(line);
                }
            }
        }
    }

    public List<String> getAnagrams(String sorted) {
        return anagramSource.getOrDefault(sorted, new ArrayList<>());
    }

    public List<String> getAnagramsContaining(String sorted) {
        return anagramSource.keySet().stream()
                .filter(key -> key.startsWith(sorted))
                .map(anagramSource::get)
                .flatMap(List::stream)
                .sorted()
                .collect(Collectors.toList());
    }

    public List<String> getAnagramsContainingAndMatchingRegex(String sorted, Pattern pattern) {
        return getAnagramsContaining(sorted)
                .stream()
                .filter(word -> matches(word, pattern))
                .collect(Collectors.toList());
    }

    private boolean matches(String word, Pattern pattern) {
        return pattern.matcher(word).matches();
    }



}

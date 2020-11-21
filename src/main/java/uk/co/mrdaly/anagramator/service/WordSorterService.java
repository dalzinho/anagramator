package uk.co.mrdaly.anagramator.service;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class WordSorterService {

    public String sortLetters(String word) {
        List<String> collect = word.chars()
                .sorted()
                .mapToObj(i -> (char) i)
                .map(String::valueOf)
                .collect(Collectors.toList());

        return String.join("", collect);
    }

}

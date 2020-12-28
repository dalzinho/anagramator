package uk.co.mrdaly.anagramator.service;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class WordNormalisationService {

    public String normalise(String s) {
        List<String> letters = s.chars()
                .mapToObj(i -> (char) i)
                .filter(Character::isAlphabetic)
                .map(String::valueOf)
                .map(String::toLowerCase)
                .map(String::trim)
                .collect(Collectors.toList());

        return String.join("", letters);
    }
}

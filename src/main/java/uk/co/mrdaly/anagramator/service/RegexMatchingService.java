package uk.co.mrdaly.anagramator.service;

import org.springframework.stereotype.Service;

@Service
public class RegexMatchingService {

    public boolean apply(String pattern, String input) {
        final String questionMarksReplaced = pattern.replace("?", "[a-z]");
        return input.matches(questionMarksReplaced);
    }
}

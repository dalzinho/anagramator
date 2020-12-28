package uk.co.mrdaly.anagramator.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class RegexMatchingService {

    @Value("${wildcard.matcher:*}")
    private String wildcardMatcher;

    public boolean apply(String pattern, String input) {
        final String questionMarksReplaced = pattern.toLowerCase().replace(wildcardMatcher, "[a-z]");
        return input.toLowerCase().matches(questionMarksReplaced);
    }

    public String prepareSqlLike(String pattern) {
        StringBuilder sb = new StringBuilder();

        boolean longWildcard = false;

        for (int i = 0; i < pattern.length(); i++) {
            boolean isWildcard = wildcardMatcher.equals(pattern.split("")[i]);

            if (!isWildcard) {
                sb.append(pattern.charAt(i));
                longWildcard = false;
            } else if (!longWildcard) {
                sb.append("%");
                longWildcard = true;
            }
        }
        return sb.toString();
    }
}

package uk.co.mrdaly.anagramator.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import uk.co.mrdaly.anagramator.model.SolutionReponse;
import uk.co.mrdaly.anagramator.service.LookupService;

import java.util.ArrayList;
import java.util.List;

@RestController
public class Controller {

    private final LookupService lookupService;

    public Controller(LookupService lookupService) {
        this.lookupService = lookupService;
    }

    @GetMapping("/solve/anagram/{word}")
    public List<SolutionReponse> getAnagramsForWord(@PathVariable String word,
                                                    @RequestParam(defaultValue = "") String pattern,
                                                    @RequestParam(defaultValue = "false") boolean partial
    ) {
        List<SolutionReponse> response = new ArrayList<>();
        if (partial) {
            response = lookupService.anagramWithPartialMatches(word);
        } else {
            if (pattern.isEmpty()) {
                response = lookupService.getAnagrams(word);
            } else {
                response = lookupService.getAnagrams(word, pattern);
            }
        }
        return response;
    }

    @GetMapping("/solve/pattern/{pattern}")
    public List<SolutionReponse> getWordsFromPattern(@PathVariable String pattern) {
        return lookupService.matchPattern(pattern);
    }


}

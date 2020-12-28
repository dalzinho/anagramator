package uk.co.mrdaly.anagramator.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import uk.co.mrdaly.anagramator.model.SolutionReponse;
import uk.co.mrdaly.anagramator.service.LookupService;

import java.util.List;

@RestController
public class Controller {

    private final LookupService lookupService;

    public Controller(LookupService lookupService) {
        this.lookupService = lookupService;
    }

    @GetMapping("/solve/anagram/{word}")
    public List<SolutionReponse> getAnagramsForWord(@PathVariable String word, @RequestParam(required = false) String pattern) {
        // todo wire this up to use the pattern if provided
            return lookupService.slightlySmarterAnagram(word);

    }

    @GetMapping("/solve/pattern/{pattern}")
    public List<SolutionReponse> getWordsFromPattern(@PathVariable String pattern) {
        return lookupService.getMatching(pattern);
    }


}

package uk.co.mrdaly.anagramator.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
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

    @GetMapping("/solve/{word}")
    public List<SolutionReponse> getAnagramsForWord(@PathVariable String word) {
            return lookupService.getAnagrams(word);
    }

    @GetMapping("/solve/{word}/{regex}")
    public List<SolutionReponse> getAnagramsForWordMatchingRegex(@PathVariable String word, @PathVariable String regex) {
        return lookupService.getAnagrams(word, regex);
    }


}

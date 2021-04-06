package uk.co.mrdaly.anagramator.service;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.co.mrdaly.anagramator.exception.AnagramatorException;
import uk.co.mrdaly.anagramator.jpa.entity.SolverEntry;
import uk.co.mrdaly.anagramator.jpa.repository.SolverEntryRepository;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static uk.co.mrdaly.anagramator.source.InputSource.WIKIPEDIA;

@Service
@Slf4j
public class WikiReaderService {

    private final SolverEntryRepository solverEntryRepository;
    private final PrimeService primeService;

    // todo wire up the normalisation service
    public WikiReaderService(SolverEntryRepository solverEntryRepository, PrimeService primeService) {
        this.solverEntryRepository = solverEntryRepository;
        this.primeService = primeService;
    }

    @Transactional
    public void updatePageSources() throws IOException {
        String allArticlesUri = "https://en.wikipedia.org/wiki/Wikipedia:Vital_articles/List_of_all_articles";
        final Connection connection = Jsoup.connect(allArticlesUri).maxBodySize(0)
                .timeout(600000);
        final Document document = connection.get();
        final Elements links = document.getElementsByTag("a");

        final List<SolverEntry> pages = links.parallelStream()
                .map(this::processWikipediaSolverInput)
                .flatMap(List::stream)
                .collect(Collectors.toList());

        solverEntryRepository.saveAll(pages);
    }

    private boolean startsWithANumber(String text) {
        Pattern pattern = Pattern.compile("[0-9].*");
        return pattern.matcher(text).matches();
    }

    private List<SolverEntry> processWikipediaSolverInput(Element wikipediaLink) {
        List<SolverEntry> solverEntries = new ArrayList<>();

        String title = wikipediaLink.attr("title");

        if (startsWithANumber(title) || title.isEmpty()) {
            log.error("Rejected: {}", title);
            return Collections.emptyList();
        }

        String urlComponent = wikipediaLink.attr("href");

        try {
            SolverEntry solverEntry = buildStubSolverEntryFrom(title, urlComponent);
            String lookupValue = preprocessLookupFields(title);

            if (lookupValue.isEmpty()) {
                System.out.println("hello");
            }
            solverEntry.setTrimmedText(lookupValue);
            solverEntry.setPrimeProduct(primeService.calculatePrimeSumForWord(lookupValue));

            solverEntries.add(solverEntry);

            final Matcher matcher = checkForApparentClosingDisambiguation(title);

            if (matcher.matches()) {
                SolverEntry secondEntry = buildStubSolverEntryFrom(title, urlComponent);
                String lookup = preprocessLookupFields(matcher.group());

                secondEntry.setTrimmedText(lookup);
                solverEntry.setPrimeProduct(primeService.calculatePrimeSumForWord(lookup));
            }
        } catch (AnagramatorException e) {
            log.error("something of a disaster when trying to process {}, {}", title, urlComponent, e);
        }

        return solverEntries;
    }

    private SolverEntry buildStubSolverEntryFrom(String title, String urlComponent) {
        SolverEntry solverEntry = new SolverEntry();
        solverEntry.setInputSource(WIKIPEDIA);
        solverEntry.setPageGrouping(title);
        solverEntry.setUri(WIKIPEDIA.getUriBase() + urlComponent);
        solverEntry.setText(title);
        return solverEntry;
    }

    private Matcher checkForApparentClosingDisambiguation(String title) {
        final Pattern bracketedClosingDisambiguationPattern = Pattern.compile("(.*)\\(.*\\)");
        return bracketedClosingDisambiguationPattern.matcher(title);
    }

    private String preprocessLookupFields(String s) {
        List<String> letters = s.chars()
                .mapToObj(i -> (char) i)
                .filter(Character::isAlphabetic)
                .map(String::valueOf)
                .map(String::toLowerCase)
                .map(String::trim)
                .map(StringUtils::stripAccents)
                .collect(Collectors.toList());

        return String.join("", letters);
    }
}


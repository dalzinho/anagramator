package uk.co.mrdaly.anagramator.service;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;
import uk.co.mrdaly.anagramator.jpa.entity.SolverEntry;
import uk.co.mrdaly.anagramator.jpa.repository.SolverEntryRepository;
import uk.co.mrdaly.anagramator.source.InputSource;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import static uk.co.mrdaly.anagramator.source.InputSource.WIKIPEDIA;

@Service
public class WikiReaderService {

    private final WordSorterService wordSorterService;
    private final SolverEntryRepository solverEntryRepository;

    public static final String WIKIBASE = "https://en.wikipedia.org";

    public WikiReaderService(WordSorterService wordSorterService, SolverEntryRepository solverEntryRepository) {
        this.wordSorterService = wordSorterService;
        this.solverEntryRepository = solverEntryRepository;
    }

    public void updatePageSources() throws IOException {
        final Connection connect = Jsoup.connect("https://en.wikipedia.org/wiki/Wikipedia:Contents/A–Z_index");
        final Document document = connect.get();

        final Element toc = document.getElementById("toc");

        final Elements elementsMatchingText = toc.getElementsMatchingText("[A-Za-z]{2}");

        for (Element element : elementsMatchingText) {
            final String href = element.attr("href");

            if (href.isEmpty()) continue;

            final Document articleListDocument = Jsoup.connect(WIKIBASE + href).get();
            final Element articleList = articleListDocument.getElementsByClass("mw-allpages-chunk").get(0);
            final Elements listItems = articleList.getElementsByTag("li");

            final List<SolverEntry> groupedPages = listItems.stream()
                    .map(li -> {
                        SolverEntry solverEntry = new SolverEntry();
                        solverEntry.setPageGrouping(element.text());

                        String sorted = wordSorterService.sortLetters(preprocessLookupFields(li.text()));

                        solverEntry.setSortedText(sorted);
                        solverEntry.setTrimmedText(preprocessLookupFields(element.text()));
                        solverEntry.setText(li.text());
                        solverEntry.setInputSource(WIKIPEDIA);

                        final String uri = li.getElementsByTag("a").get(0).attr("href");
                        solverEntry.setUri(WIKIPEDIA.getUriBase() + uri);

                        return solverEntry;
                    })
                    .collect(Collectors.toList());

            solverEntryRepository.saveAll(groupedPages);
        }
    }

    private String preprocessLookupFields(String s) {
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

package uk.co.mrdaly.anagramator.config;

import net.sourceforge.jwbf.core.actions.HttpActionClient;
import net.sourceforge.jwbf.mediawiki.bots.MediaWikiBot;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

//@Configuration
public class WikiReaderConfig {

    @Bean
    public MediaWikiBot mediaWikiBot(@Value("${api.base.wikipedia}") String wikipediaBase) {
        HttpActionClient client = HttpActionClient.builder()
                .withUrl(wikipediaBase)
                .withUserAgent("Anagramatorbot/0.0.1 (http://example.com/anagramator) jwfs/2.1.1", "1.0", "dalzinho")
                .withRequestsPerUnit(10, TimeUnit.MINUTES)
                .build();

        return new MediaWikiBot(client);
    }
}

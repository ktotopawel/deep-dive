package com.ktotopawel.deepdive.infrastructure;

import com.ktotopawel.deepdive.domain.model.Article;
import com.ktotopawel.deepdive.domain.logic.FeedFetchException;
import com.ktotopawel.deepdive.domain.port.FeedFetcher;
import com.rometools.rome.feed.synd.SyndFeed;
import com.rometools.rome.io.FeedException;
import com.rometools.rome.io.SyndFeedInput;
import org.springframework.stereotype.Service;
import org.xml.sax.InputSource;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

@Service
public class RomeRssFetcher implements FeedFetcher {

    @Override
    public List<Article> fetch(String url) {
        try (InputStream in = new URI(url).toURL().openStream()) {
            InputSource inputSource = new InputSource(in);
            SyndFeed feed = new SyndFeedInput().build(inputSource);
            return feed.getEntries().stream()
                    .map((entry) -> new Article(
                            entry.getTitle(),
                            entry.getLink(),
                            url,
                            entry.getPublishedDate().toInstant(),
                            extractContent(entry)))
                    .toList();
        } catch (URISyntaxException | IOException | FeedException e) {
            throw new FeedFetchException("Issue while parsing url: " + url, e);
        }
    }

    private String extractContent(com.rometools.rome.feed.synd.SyndEntry entry) {
        if (entry.getContents() != null && !entry.getContents().isEmpty()) {
            return entry.getContents().getFirst().getValue();
        }
        if (entry.getDescription() != null) {
            return entry.getDescription().getValue();
        }
        return "";
    }
}

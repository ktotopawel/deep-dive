package com.ktotopawel.deepdive.infrastructure;

import com.ktotopawel.deepdive.domain.Article;
import com.ktotopawel.deepdive.domain.FeedFetchException;
import com.ktotopawel.deepdive.domain.FeedFetcher;
import com.rometools.rome.feed.synd.SyndEntry;
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
import java.util.Optional;

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
                            entry.getPublishedDate().toInstant(),
                            extractContent(entry)))
                    .toList();
        } catch (URISyntaxException | IOException | FeedException e) {
            throw new FeedFetchException("Issue while parsing url: " + url, e);
        }
    }

    private String extractContent(com.rometools.rome.feed.synd.SyndEntry entry) {
        if (entry.getContents() != null && !entry.getContents().isEmpty()) {
            return entry.getContents().get(0).getValue();
        }
        if (entry.getDescription() != null) {
            return entry.getDescription().getValue();
        }
        return "";
    }
}

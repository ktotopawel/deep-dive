package com.ktotopawel.deepdive.infrastructure;

import com.ktotopawel.deepdive.domain.logic.FeedFetchException;
import com.ktotopawel.deepdive.domain.model.Article;
import com.ktotopawel.deepdive.domain.port.FeedFetcher;
import com.rometools.rome.feed.synd.SyndFeed;
import com.rometools.rome.io.FeedException;
import com.rometools.rome.io.SyndFeedInput;
import org.springframework.stereotype.Service;
import org.xml.sax.InputSource;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

@Service
public class RomeRssFetcher implements FeedFetcher {

    @Override
    public List<Article> fetch(String url) {
        HttpURLConnection connection = null;

        try {
            URI uri = new URI(url);
            connection = (HttpURLConnection) uri.toURL().openConnection();
            connection.setRequestMethod("GET");


            connection.setRequestProperty("User-Agent",
                    "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0 Safari/537.36");
            connection.setRequestProperty("Accept", "application/rss+xml, application/atom+xml, application/xml;q=0.9, */*;q=0.8");
            connection.setRequestProperty("Accept-Language", "en-US,en;q=0.9");
            connection.setConnectTimeout(10_000);
            connection.setReadTimeout(10_000);
            connection.setInstanceFollowRedirects(true);

            int code = connection.getResponseCode();
            InputStream stream = (code >= 200 && code < 300) ? connection.getInputStream() : connection.getErrorStream();

            if (code < 200 || code >= 300) {
                String body = (stream != null) ? new String(stream.readAllBytes()) : "";
                throw new IOException("HTTP error code : " + code + " from: " + url + "\n" + body);
            }
            try (InputStream in = stream) {
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
            }
        } catch (URISyntaxException | IOException | FeedException e) {
            throw new FeedFetchException("Issue while parsing url: " + url, e);
        } finally {
            if (connection != null) connection.disconnect();
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

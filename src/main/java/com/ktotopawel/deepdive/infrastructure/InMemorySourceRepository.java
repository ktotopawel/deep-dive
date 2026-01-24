package com.ktotopawel.deepdive.infrastructure;

import com.ktotopawel.deepdive.domain.model.Source;
import com.ktotopawel.deepdive.domain.port.SourceRepository;
import com.rometools.rome.io.FeedException;
import com.rometools.rome.io.SyndFeedInput;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.xml.sax.InputSource;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;

@Repository
@RequiredArgsConstructor
public class InMemorySourceRepository implements SourceRepository {

    private Map<String, Source> sources;

    @Override
    public Source getOrSave(String url) {
        if (sources.containsKey(url)) {
            return sources.get(url);
        }
        Source newSource = new Source(getSoruceName(url), url);
        sources.put(url, newSource);
        return newSource;
    }

    private String getSoruceName(String url) {
        try {
            return new SyndFeedInput().build(new InputSource(new URI(url).toURL().openStream())).getTitle();
        } catch (IOException | URISyntaxException | FeedException e) {
            return url;
        }
    }
}

package com.ktotopawel.deepdive.ingestion.adapter.persistence;

import com.ktotopawel.deepdive.ingestion.domain.model.Source;
import com.ktotopawel.deepdive.ingestion.domain.port.SourceRepository;
import com.rometools.rome.io.FeedException;
import com.rometools.rome.io.SyndFeedInput;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;
import org.xml.sax.InputSource;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Optional;

@Primary
@Repository
@RequiredArgsConstructor
public class JpaSourceRepository implements SourceRepository {

    private final SpringDataSourceRepository repository;

    @Override
    public Source getOrSave(String url) {
        Optional<SourceEntity> source = repository.findById(url);
        if (source.isEmpty()) {
            SourceEntity newSource = new SourceEntity();
            newSource.setUrl(url);
            newSource.setName(getSourceName(url));
            repository.save(newSource);
            return new Source(newSource.getName(),  newSource.getUrl());
        }
        return new Source(source.get().getName(),  source.get().getUrl());
    }

    private String getSourceName(String url) {
        try {
            return new SyndFeedInput().build(new InputSource(new URI(url).toURL().openStream())).getTitle();
        } catch (IOException | URISyntaxException | FeedException e) {
            return url;
        }
    }
}

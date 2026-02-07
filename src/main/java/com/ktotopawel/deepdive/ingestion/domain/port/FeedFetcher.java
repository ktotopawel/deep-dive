package com.ktotopawel.deepdive.ingestion.domain.port;

import com.ktotopawel.deepdive.ingestion.domain.model.Article;

import java.util.List;

public interface FeedFetcher {
    public List<Article> fetch(String url);
}

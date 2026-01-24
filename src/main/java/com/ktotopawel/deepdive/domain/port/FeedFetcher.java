package com.ktotopawel.deepdive.domain.port;

import com.ktotopawel.deepdive.domain.model.Article;

import java.util.List;

public interface FeedFetcher {
    public List<Article> fetch(String url);
}

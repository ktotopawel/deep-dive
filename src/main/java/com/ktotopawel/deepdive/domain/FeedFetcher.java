package com.ktotopawel.deepdive.domain;

import java.util.List;

public interface FeedFetcher {
    public List<Article> fetch(String url);
}

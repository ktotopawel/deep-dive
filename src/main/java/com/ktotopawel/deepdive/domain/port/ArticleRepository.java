package com.ktotopawel.deepdive.domain.port;

import com.ktotopawel.deepdive.domain.model.Article;
import com.ktotopawel.deepdive.domain.model.Source;

import java.util.List;

public interface ArticleRepository {
    public void save(Article article);
    public List<Article> fetchAllFromSource(Source source);
}

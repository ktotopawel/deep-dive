package com.ktotopawel.deepdive.infrastructure;

import com.ktotopawel.deepdive.domain.model.Article;
import com.ktotopawel.deepdive.domain.model.Source;
import com.ktotopawel.deepdive.domain.port.ArticleRepository;
import com.ktotopawel.deepdive.domain.port.SourceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Objects;

@Repository
@RequiredArgsConstructor
public class InMemoryArticleRepository implements ArticleRepository {

    private final HashMap<String, Article> articles = new HashMap<>();

    @Override
    public void save(Article article) {
        this.articles.putIfAbsent(article.url(), article);
    }

    @Override
    public List<Article> fetchAllFromSource(Source source) {
        return articles.values().stream()
                .filter(article -> article.sourceUrl().equals(source.url()))
                .toList();
    }
}
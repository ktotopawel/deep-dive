package com.ktotopawel.deepdive.ingestion.domain.service;

import com.ktotopawel.deepdive.ingestion.domain.service.filter.Filter;
import com.ktotopawel.deepdive.ingestion.domain.model.Article;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@RequiredArgsConstructor
@Component
public class FeedRefinery {

    private final List<Filter> filters;

    public List<Article> refine(List<Article> articles) {
        return articles.stream()
                .filter((article -> filters.stream().noneMatch(f -> f.test(article))))
                .toList();
    }
}

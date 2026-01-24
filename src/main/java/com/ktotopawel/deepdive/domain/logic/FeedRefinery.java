package com.ktotopawel.deepdive.domain.logic;

import com.ktotopawel.deepdive.domain.logic.filter.Filter;
import com.ktotopawel.deepdive.domain.model.Article;
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

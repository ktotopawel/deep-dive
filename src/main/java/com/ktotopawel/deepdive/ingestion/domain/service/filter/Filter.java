package com.ktotopawel.deepdive.ingestion.domain.service.filter;

import com.ktotopawel.deepdive.ingestion.domain.model.Article;

import java.util.List;
import java.util.function.Predicate;
import java.util.function.ToIntFunction;

public abstract class Filter implements Predicate<Article> {

    private static final int THRESHOLD = 50;

    @Override
    public boolean test(Article article) {
        List<ToIntFunction<Article>> heuristics = getHeuristics();
        int totalScore = heuristics.stream()
                .mapToInt((h) -> h.applyAsInt(article))
                .sum();
        return totalScore >= THRESHOLD;
    }

    protected abstract List<ToIntFunction<Article>> getHeuristics();
}
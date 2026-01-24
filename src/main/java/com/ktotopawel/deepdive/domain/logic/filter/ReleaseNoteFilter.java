package com.ktotopawel.deepdive.domain.logic.filter;

import com.ktotopawel.deepdive.domain.model.Article;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.function.ToIntFunction;

@Component
public class ReleaseNoteFilter extends Filter {

    private final List<ToIntFunction<Article>> heuristics = List.of(
            this::scoreTitle,
            this::scoreUrl,
            this::scoreContent
    );

    @Override
    protected List<ToIntFunction<Article>> getHeuristics() {
        return heuristics;
    }

    private int scoreTitle(Article article) {
        String title = article.title().toLowerCase();
        if (title.matches(".*v?\\d+\\.\\d+.*")) return 50;
        if (title.contains("release") || title.contains("changelog") || title.contains("what's new")) return 40;
        return 0;
    }

    private int scoreUrl(Article article) {
        String url = article.url().toLowerCase();
        if (url.contains("/releases") || url.contains("changelog")) return 30;
        return 0;
    }

    private int scoreContent(Article article) {
        String content = article.content().toLowerCase();
        if (content.contains("bug fixes") || content.contains("new features") || content.contains("breaking changes")) return 20;
        if (content.contains("full changelog")) return 15;
        return 0;
    }
}

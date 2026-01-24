package com.ktotopawel.deepdive.domain.logic.filter;

import com.ktotopawel.deepdive.domain.model.Article;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.function.ToIntFunction;

@Component
public class TutorialFilter extends Filter {

    private final List<ToIntFunction<Article>> heuristics = List.of(
            this::scoreTitle,
            this::scoreCodeDensity,
            this::scoreExternalLinks,
            this::scoreStepMarkers
    );

    @Override
    protected List<ToIntFunction<Article>> getHeuristics() {
        return heuristics;
    }

    private int scoreTitle(Article article) {
        String title = article.title().toLowerCase();
        if (title.contains("how to") || title.contains("guide")) return 30;
        if (title.contains("tutorial") || title.contains("build")) return 40;
        return 0;
    }

    private int scoreCodeDensity(Article article) {
        long codeBlocks = article.content().lines()
                .filter(line -> line.contains("<pre") || line.contains("<code"))
                .count();

        if (codeBlocks > 5) return 30;
        if (codeBlocks > 1) return 15;
        return 0;
    }

    private int scoreStepMarkers(Article article) {
        String content = article.content().toLowerCase();
        if (content.contains("step 1") || content.contains("step 01")) return 20;
        if (content.contains("<ol>")) return 10;
        return 0;
    }

    private int scoreExternalLinks(Article article) {
        String content = article.content().toLowerCase();
        if (content.contains("github.com") || content.contains("gitlab.com")) return 15;
        return 0;
    }
}
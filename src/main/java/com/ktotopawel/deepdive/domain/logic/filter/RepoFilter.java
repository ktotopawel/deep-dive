package com.ktotopawel.deepdive.domain.logic.filter;

import com.ktotopawel.deepdive.domain.model.Article;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.function.ToIntFunction;

@Component
public class RepoFilter extends Filter {

    private final List<ToIntFunction<Article>> heuristics = List.of(
            this::scoreUrl,
            this::scoreTitle,
            this::scoreContent
    );

    @Override
    protected List<ToIntFunction<Article>> getHeuristics() {
        return heuristics;
    }

    private int scoreUrl(Article article) {
        String url = article.url().toLowerCase();
        if (url.contains("github.com") || url.contains("gitlab.com") || url.contains("bitbucket.org")) {
            return 60;
        }
        return 0;
    }

    private int scoreTitle(Article article) {
        String title = article.title().toLowerCase();
        if (title.contains("repository") || title.contains("source code")) return 30;
        if (title.contains("library") || title.contains("sdk") || title.contains("framework")) return 20;
        return 0;
    }

    private int scoreContent(Article article) {
        String content = article.content().toLowerCase();
        if (content.contains("npm install") || content.contains("mvn install") || 
            content.contains("pip install") || content.contains("cargo build") || 
            content.contains("docker run")) {
            return 25;
        }
        if (content.contains("installation") && content.contains("usage")) {
            return 15;
        }
        return 0;
    }
}

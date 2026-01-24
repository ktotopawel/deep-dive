package com.ktotopawel.deepdive.application;

import com.ktotopawel.deepdive.domain.Article;
import com.ktotopawel.deepdive.domain.FeedFetcher;
import com.ktotopawel.deepdive.domain.FeedRefinery;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class DeepDiveRunner implements CommandLineRunner {

    private final FeedFetcher feedFetcher;
    private final FeedRefinery feedRefinery;

    @Override
    public void run(String... args) {
        if (args.length < 1) {
            System.out.println("Usage: DeepDiveRunner <url>");
            return;
        }
        String url = args[0];
        System.out.println("Fetching feed from: " + url);

        List<Article> articles = feedFetcher.fetch(url);

        System.out.println("Found " + articles.size() + " articles");

        List<Article> refinedArticles = feedRefinery.refine(articles);

        System.out.println("Refined feeds: " + refinedArticles);
        refinedArticles.forEach(System.out::println);
        System.exit(0);
    }

}

package com.ktotopawel.deepdive.application;

import com.ktotopawel.deepdive.ingestion.domain.model.Article;
import com.ktotopawel.deepdive.ingestion.domain.model.Source;
import com.ktotopawel.deepdive.ingestion.domain.port.ArticleRepository;
import com.ktotopawel.deepdive.ingestion.domain.port.FeedFetcher;
import com.ktotopawel.deepdive.ingestion.domain.service.FeedRefinery;
import com.ktotopawel.deepdive.ingestion.domain.port.SourceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class DeepDiveRunner implements CommandLineRunner {

    private final FeedFetcher feedFetcher;
    private final FeedRefinery feedRefinery;
    private final ArticleRepository articleRepository;
    private final SourceRepository sourceRepository;

    @Override
    public void run(String... args) {
        if (args.length < 1) {
            System.out.println("Usage: DeepDiveRunner <url>");
            return;
        }
        String url = args[0];
        System.out.println("Fetching feed from: " + url);

        List<Article> articles = feedFetcher.fetch(url);
        List<Article> refinedArticles = feedRefinery.refine(articles);
        refinedArticles.forEach(articleRepository::save);
        Source source = sourceRepository.getOrSave(url);
        articleRepository.fetchAllFromSource(source).forEach(System.out::println);

        System.exit(0);
    }

}

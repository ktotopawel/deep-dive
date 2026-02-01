package com.ktotopawel.deepdive.infrastructure;

import com.ktotopawel.deepdive.domain.model.Article;
import com.ktotopawel.deepdive.domain.model.Source;
import com.ktotopawel.deepdive.domain.port.ArticleRepository;
import com.ktotopawel.deepdive.infrastructure.persistance.ArticleEntity;
import com.ktotopawel.deepdive.infrastructure.persistance.SpringDataArticleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;

import java.util.List;

@Primary
@Repository
@RequiredArgsConstructor
public class JpaArticleRepository implements ArticleRepository {

    private final SpringDataArticleRepository repository;

    public void save(Article article) {
        ArticleEntity articleEntity = new ArticleEntity();
        articleEntity.setTitle(article.title());
        articleEntity.setContent(article.content());
        articleEntity.setUrl(article.url());
        articleEntity.setSourceUrl(article.sourceUrl());
        articleEntity.setPublishedAt(article.publishedAt());
        repository.save(articleEntity);
    }

    @Override
    public List<Article> fetchAllFromSource(Source source) {
        return repository
                .findAllBySourceUrl(source.url()).stream()
                .map(articleEntity -> new Article(
                        articleEntity.getTitle(),
                        articleEntity.getUrl(),
                        articleEntity.getSourceUrl(),
                        articleEntity.getPublishedAt(),
                        articleEntity.getContent()
                ))
                .toList();
    }
}

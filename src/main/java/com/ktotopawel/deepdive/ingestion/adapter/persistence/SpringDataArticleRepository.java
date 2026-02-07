package com.ktotopawel.deepdive.ingestion.adapter.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SpringDataArticleRepository extends JpaRepository<ArticleEntity, String> {
    List<ArticleEntity> findAllBySourceUrl(String sourceUrl);
}

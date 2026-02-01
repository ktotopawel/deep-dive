package com.ktotopawel.deepdive.infrastructure.persistance;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
@Entity
@Table(name = "article_entity")
public class ArticleEntity {

    @Id
    @Column(nullable = false, length = 1024)
    private String title;

    @Column(nullable = false)
    private String url;

    @Column(nullable = false)
    private String sourceUrl;

    @Column(nullable = false)
    private Instant publishedAt;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

}
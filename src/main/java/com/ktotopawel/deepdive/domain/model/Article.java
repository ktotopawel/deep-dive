package com.ktotopawel.deepdive.domain.model;

import org.jetbrains.annotations.NotNull;

import java.time.Instant;

public record Article(String title, String url, String sourceUrl, Instant publishedAt, String content) {
    @Override
    @NotNull
    public String toString() {
        return url + '\n' + title + " " + publishedAt + '\n' + '\n' + content + '\n';
    }
}

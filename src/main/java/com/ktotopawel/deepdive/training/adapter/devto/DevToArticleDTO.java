package com.ktotopawel.deepdive.training.adapter.devto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonKey;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.Instant;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record DevToArticleDTO(
        Long id,
        String url,
        String title,
        @JsonProperty("body_markdown") String body,
        List<String> tags,
        @JsonProperty("published_timestamp") Instant publishedAt
) {
}

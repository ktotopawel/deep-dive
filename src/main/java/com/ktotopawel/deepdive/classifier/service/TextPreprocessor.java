package com.ktotopawel.deepdive.classifier.service;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;

import lombok.Getter;
import org.springframework.stereotype.Service;

import com.ktotopawel.deepdive.classifier.model.TrainingArticle;
import com.ktotopawel.deepdive.classifier.repository.TrainingArticleRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TextPreprocessor {

    private final Map<String, Map<String, Long>> titleWordPerCategoryMap = new HashMap<>();

    private final Set<String> titleVocabulary = new HashSet<>();

    private final Map<String, Long> titleTokenCountPerCategory = new HashMap<>();

    private final Map<String, Map<String, Long>> bodyWordPerCategoryMap = new HashMap<>();

    private final Set<String> bodyVocabulary = new HashSet<>();

    private final Map<String, Long> bodyTokenCountPerCategory = new HashMap<>();

    private final Map<String, Long> categoryCounts = new HashMap<>();

    private final long totalDocumentCount = 0L;

    private final boolean isProcessed = false;

    private final TrainingArticleRepository taRepository;

    public Long getWordCount(String category, String word) {
        if (!isProcessed)
            this.ingestArticles();
        return bodyWordPerCategoryMap.get(category).get(word);
    }

    private void ingestArticles() {
        List<TrainingArticle> articles = taRepository.findAll();

        articles.forEach(a -> processArticle(a));
    }

    private void processArticle(TrainingArticle article) {
        String category = article.getCategory();

        Map<String, Long> titleMap = titleWordPerCategoryMap.computeIfAbsent(category, k -> new HashMap<>());
        Map<String, Long> bodyMap = bodyWordPerCategoryMap.computeIfAbsent(category, k -> new HashMap<>());

        tokenizeString(article.getTitle(), titleVocabulary, token -> {
            titleMap.merge(token, 1L, Long::sum);
            titleTokenCountPerCategory.merge(token, 1L, Long::sum);
        });

        tokenizeString(article.getBody(), bodyVocabulary, token -> {
            bodyMap.merge(token, 1L, Long::sum);
            bodyTokenCountPerCategory.merge(token, 1L, Long::sum);
        });

        categoryCounts.merge(category, 1L, Long::sum);
    }

    private void tokenizeString(String text, Set<String> vocabularyAccumulator, Consumer<String> tokenConsumer) {
        StringBuilder current = new StringBuilder();

        for (int i = 0; i < text.length(); i++) {
            char ch = text.charAt(i);
            if (Character.isLetter(ch)) {
                current.append(ch);
            } else {
                if (!current.isEmpty()) {
                    String word = current.toString();
                    vocabularyAccumulator.add(word);
                    tokenConsumer.accept(word);
                    current = new StringBuilder();
                }
            }
        }

        if (!current.isEmpty()) {
            String word = current.toString();
            vocabularyAccumulator.add(word);
            tokenConsumer.accept(word);
        }

    }
}

package com.ktotopawel.deepdive.classifier.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.ktotopawel.deepdive.classifier.model.TrainingArticle;
import com.ktotopawel.deepdive.classifier.repository.TrainingArticleRepository;
import com.univocity.parsers.common.ParsingContext;
import com.univocity.parsers.common.processor.BeanListProcessor;
import com.univocity.parsers.csv.CsvParser;
import com.univocity.parsers.csv.CsvParserSettings;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Service
@Slf4j
public class DataSetLoader {

  private final TrainingArticleRepository repository;

  private static final int BATCH_SIZE = 50;

  public void parse(String fileUrl, Character separator) {
    List<TrainingArticle> buffer = new ArrayList<>();

    try (Reader inputReader = new InputStreamReader(
        new FileInputStream(
            new File(fileUrl)),
        "UTF-8")) {
      BeanListProcessor<TrainingArticle> rowProcessor = new BeanListProcessor<TrainingArticle>(TrainingArticle.class) {

        @Override
        public void beanProcessed(TrainingArticle article, ParsingContext context) {
          if (article.getCategory() != null) {
            article.setCategory(article.getCategory().toUpperCase().trim());
          }

          if (article.getCategory().isBlank() || article.getTitle().isBlank() || article.getBody().isBlank())
            return;

          buffer.add(article);

          if (buffer.size() >= BATCH_SIZE) {
            repository.saveAll(buffer);
            buffer.clear();
          }
        }

        @Override
        public void processEnded(ParsingContext context) {

          if (!buffer.isEmpty()) {
            repository.saveAll(buffer);
            buffer.clear();
          }
        }
      };
      CsvParserSettings settings = new CsvParserSettings();
      settings.setHeaderExtractionEnabled(true);
      settings.setProcessor(rowProcessor);
      settings.getFormat().setDelimiter(separator);
      CsvParser parser = new CsvParser(settings);
      parser.parse(inputReader);
    } catch (IOException e) {
      log.error(e.toString());
    }
  }
}

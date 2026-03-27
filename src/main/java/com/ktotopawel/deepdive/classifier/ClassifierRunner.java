package com.ktotopawel.deepdive.classifier;

import java.util.Set;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import com.ktotopawel.deepdive.classifier.repository.TrainingArticleRepository;
import com.ktotopawel.deepdive.classifier.service.DataSetLoader;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Profile("classifier")
@Slf4j
class ClassifierRunner implements CommandLineRunner {

  private final DataSetLoader loader;

  private final TrainingArticleRepository repository;

  private final static Set<Character> ALLOWED_SEPARATORS = Set.of(',', ';', '\t');

  @Override
  public void run(String... args) throws Exception {
    repository.deleteAll();

    String csvPath = "./src/main/resources/dataset/dataset.csv";
    String separator = ",";

    if (args.length == 0) {
      log.warn("Missing argument: data.csv path. Defaulting to \"{}\"", csvPath);
    } else {
      csvPath = args[0];
    }

    if (args.length <= 1) {
      log.warn("Missing argument: csv separator. Defaulting to \"{}\"", separator);
    } else {
      separator = args[1];
    }

    if (separator.length() > 1 || !ALLOWED_SEPARATORS.contains(separator.charAt(0))) {
      log.warn("Invalid separator: \"{}\". Defaulting to \",\"", separator);
      log.info("Valid separators include: \";\", \",\", \"\\t\"");
      separator = ",";
    }

    loader.parse(csvPath, separator.charAt(0));

    System.exit(0);
  }

}

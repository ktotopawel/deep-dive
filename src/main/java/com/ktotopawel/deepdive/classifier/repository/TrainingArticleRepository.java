package com.ktotopawel.deepdive.classifier.repository;

import org.springframework.data.repository.ListCrudRepository;

import com.ktotopawel.deepdive.classifier.model.TrainingArticle;

public interface TrainingArticleRepository extends ListCrudRepository<TrainingArticle, Long> {

}

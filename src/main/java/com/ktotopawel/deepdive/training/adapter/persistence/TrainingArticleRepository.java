package com.ktotopawel.deepdive.training.adapter.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

public interface TrainingArticleRepository extends JpaRepository<TrainingArticleEntity, String> {
}

package com.ktotopawel.deepdive.classifier.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

// probably could be extracted to a different schema is psql. 
// for simplicity sake, kept as a table

@Entity
@Table(name = "training_article")
@Getter
@Setter
@NoArgsConstructor
public class TrainingArticle {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false)
  private String body;

  @Column(nullable = false)
  @Enumerated(EnumType.STRING)
  private Category category;
}

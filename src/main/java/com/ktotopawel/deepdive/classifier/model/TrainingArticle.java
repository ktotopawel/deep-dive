package com.ktotopawel.deepdive.classifier.model;

import com.univocity.parsers.annotations.Parsed;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

// probably could be extracted to a different schema is psql. 
// for simplicity sake, kept as a table

@Entity
@Table(name = "training_article")
@Getter
@Setter
@NoArgsConstructor
public class TrainingArticle {

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE)
  private Long id;

  @Column(nullable = false)
  @Parsed(field = "title")
  private String title;

  @Column(nullable = false, columnDefinition = "TEXT")
  @Parsed(field = "body")
  private String body;

  @Column(nullable = false)
  @Parsed(field = "category")
  private String category;
}

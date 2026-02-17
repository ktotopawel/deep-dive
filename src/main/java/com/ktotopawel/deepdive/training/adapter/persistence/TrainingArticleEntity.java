package com.ktotopawel.deepdive.training.adapter.persistence;

import com.ktotopawel.deepdive.training.domain.model.Label;
import com.ktotopawel.deepdive.training.domain.model.LabelSource;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "training_article_entity")
public class TrainingArticleEntity {

    @Id
    @Column(nullable = false, updatable = false)
    String url;

    @Column
    String title;

    @Column(nullable = false, columnDefinition = "text")
    String body;

    @Column(name = "label_source")
    @Enumerated(EnumType.STRING)
    LabelSource labelSource;

    @Column
    @Enumerated(EnumType.STRING)
    Label label;
}

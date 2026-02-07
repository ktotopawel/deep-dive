package com.ktotopawel.deepdive.ingestion.adapter.persistence;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "source_entity")
public class SourceEntity {

    @Id
    @Column(nullable = false, length = 1024)
    private String url;

    @Column(nullable = false)
    private String name;
}
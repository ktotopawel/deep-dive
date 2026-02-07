package com.ktotopawel.deepdive.ingestion.adapter.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

public interface SpringDataSourceRepository extends JpaRepository<SourceEntity, String> {
}

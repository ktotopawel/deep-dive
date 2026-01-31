package com.ktotopawel.deepdive.infrastructure.persistance;

import org.springframework.data.jpa.repository.JpaRepository;

public interface SpringDataSourceRepository extends JpaRepository<SourceEntity, String> {
}

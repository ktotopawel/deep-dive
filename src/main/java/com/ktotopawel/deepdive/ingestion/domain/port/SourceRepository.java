package com.ktotopawel.deepdive.ingestion.domain.port;

import com.ktotopawel.deepdive.ingestion.domain.model.Source;

public interface SourceRepository {

    public Source getOrSave(String url);
}

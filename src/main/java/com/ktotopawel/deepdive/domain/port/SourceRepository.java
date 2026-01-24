package com.ktotopawel.deepdive.domain.port;

import com.ktotopawel.deepdive.domain.model.Source;

public interface SourceRepository {

    public Source getOrSave(String url);
}

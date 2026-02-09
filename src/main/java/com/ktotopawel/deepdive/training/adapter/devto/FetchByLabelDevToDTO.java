package com.ktotopawel.deepdive.training.adapter.devto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.ktotopawel.deepdive.training.domain.model.Label;

import java.awt.*;

@JsonIgnoreProperties(ignoreUnknown = true)
public record FetchByLabelDevToDTO(
        Long id
) {
}

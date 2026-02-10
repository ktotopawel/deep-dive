package com.ktotopawel.deepdive.training.adapter.devto.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;


@ConfigurationProperties(prefix = "devto")
@Component
@Getter
@Setter
public class DevToClientConfig {

    private int concurrentCalls = 6;
    private int perPage = 100;
    private int maxPages = 10;
    private int blockFor = 10;
    private int maxRetries = 10;
    private int baseBackoff = 1;
    private int maxBackoff = 10;

}

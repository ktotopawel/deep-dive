package com.ktotopawel.deepdive.ingestion.domain.service;

public class FeedFetchException extends RuntimeException {
    public FeedFetchException(String message, Throwable cause) {
        super(message, cause);
    }
}

package com.ktotopawel.deepdive.training.domain.model;

import lombok.Getter;

@Getter
public enum Label {
    TUTORIAL("tutorial"),
    RELEASE_NOTES("release_notes"),
    PROGRAMMING("programming");

    private final String tag;

    Label(String tag){
        this.tag = tag;
    };
}
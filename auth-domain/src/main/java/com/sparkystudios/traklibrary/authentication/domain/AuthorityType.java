package com.sparkystudios.traklibrary.authentication.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum AuthorityType {

    DELETE((short)0),
    READ((short)1),
    WRITE((short)2);

    private final short id;
}

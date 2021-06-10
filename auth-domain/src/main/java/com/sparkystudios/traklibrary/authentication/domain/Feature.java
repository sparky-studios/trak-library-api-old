package com.sparkystudios.traklibrary.authentication.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Feature {

    USERS((short)0),
    GAMES((short)1);

    private final short id;
}

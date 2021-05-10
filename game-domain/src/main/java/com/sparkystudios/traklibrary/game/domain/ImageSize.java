package com.sparkystudios.traklibrary.game.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ImageSize {

    SMALL((short)0),
    MEDIUM((short)1),
    LARGE((short)2);

    private final short id;
}

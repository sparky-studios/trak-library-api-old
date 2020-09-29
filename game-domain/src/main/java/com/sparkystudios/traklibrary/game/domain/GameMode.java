package com.sparkystudios.traklibrary.game.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum GameMode {

    SINGLE_PLAYER((short)1),
    MULTI_PLAYER((short)2),
    COOPERATIVE((short)3),
    VIRTUAL_REALITY((short)4);

    private final short flagValue;
}

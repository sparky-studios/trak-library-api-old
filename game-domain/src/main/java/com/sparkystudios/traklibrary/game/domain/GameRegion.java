package com.sparkystudios.traklibrary.game.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum GameRegion {

    NORTH_AMERICA((short)0),
    PAL((short)1),
    JAPAN((short)2);

    private final short id;
}

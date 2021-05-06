package com.sparkystudios.traklibrary.game.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum AgeRatingClassification {

    ESRB((short)0),
    PEGI((short)1),
    CERO((short)2);

    private final short id;
}

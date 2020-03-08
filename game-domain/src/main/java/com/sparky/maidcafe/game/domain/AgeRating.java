package com.sparky.maidcafe.game.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum AgeRating {

    EVERYONE((short)0),
    EVERYONE_TEN_PLUS((short)1),
    TEEN((short)2),
    MATURE((short)3),
    ADULTS_ONLY((short)4),
    RATING_PENDING((short)5);

    private final short id;
}

package com.sparky.trak.game.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum CompanyType {

    PUBLISHER((short)0),
    DEVELOPER((short)1);

    private final short id;
}

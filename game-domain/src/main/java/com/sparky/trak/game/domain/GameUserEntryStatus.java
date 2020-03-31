package com.sparky.maidcafe.game.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum GameUserEntryStatus {

    WISH_LIST((short)0),
    BACKLOG((short)1),
    IN_PROGRESS((short)2),
    COMPLETED((short)3),
    DROPPED((short)4);

    private final short id;
}

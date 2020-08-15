package com.traklibrary.game.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum GameUserEntryStatus {

    BACKLOG((short)0),
    IN_PROGRESS((short)1),
    COMPLETED((short)2),
    DROPPED((short)3);

    private final short id;
}

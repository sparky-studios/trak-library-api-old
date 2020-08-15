package com.traklibrary.game.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum BarcodeType {

    EAN_13((short)0),
    UPC_A((short)1);

    private final short id;
}

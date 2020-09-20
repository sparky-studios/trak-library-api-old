package com.sparkystudios.traklibrary.game.service.dto;

import com.sparkystudios.traklibrary.game.domain.BarcodeType;
import lombok.Data;
import org.springframework.hateoas.server.core.Relation;

@Data
@Relation(collectionRelation = "data", itemRelation = "game-barcode")
public class GameBarcodeDto {

    private long id;

    private long gameId;

    private long platformId;

    private String barcode;

    private BarcodeType barcodeType;

    private long version;
}

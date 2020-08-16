package com.traklibrary.game.service.mapper;

import com.traklibrary.game.domain.GameBarcode;
import com.traklibrary.game.service.dto.GameBarcodeDto;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface GameBarcodeMapper {

    GameBarcodeDto gameBarcodeToGameBarcodeDto(GameBarcode gameBarcode);
}

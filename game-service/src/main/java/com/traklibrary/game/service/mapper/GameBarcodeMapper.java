package com.traklibrary.game.service.mapper;

import com.traklibrary.game.domain.GameBarcode;
import com.traklibrary.game.service.dto.GameBarcodeDto;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface GameBarcodeMapper {

    GameBarcodeMapper INSTANCE = Mappers.getMapper(GameBarcodeMapper.class);

    GameBarcodeDto gameBarcodeToGameBarcodeDto(GameBarcode gameBarcode);
}

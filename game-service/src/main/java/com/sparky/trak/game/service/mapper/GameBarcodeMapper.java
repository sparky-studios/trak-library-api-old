package com.sparky.trak.game.service.mapper;

import com.sparky.trak.game.domain.GameBarcode;
import com.sparky.trak.game.service.dto.GameBarcodeDto;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface GameBarcodeMapper {

    GameBarcodeMapper INSTANCE = Mappers.getMapper(GameBarcodeMapper.class);

    GameBarcodeDto gameBarcodeToGameBarcodeDto(GameBarcode gameBarcode);
}

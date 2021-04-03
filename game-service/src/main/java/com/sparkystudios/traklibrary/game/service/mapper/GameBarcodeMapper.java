package com.sparkystudios.traklibrary.game.service.mapper;

import com.sparkystudios.traklibrary.game.domain.GameBarcode;
import com.sparkystudios.traklibrary.game.service.dto.GameBarcodeDto;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface GameBarcodeMapper {

    GameBarcodeDto fromGameBarcode(GameBarcode gameBarcode);
}

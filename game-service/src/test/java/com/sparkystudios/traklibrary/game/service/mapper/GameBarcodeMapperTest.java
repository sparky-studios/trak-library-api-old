package com.sparkystudios.traklibrary.game.service.mapper;

import com.sparkystudios.traklibrary.game.domain.BarcodeType;
import com.sparkystudios.traklibrary.game.domain.GameBarcode;
import com.sparkystudios.traklibrary.game.service.dto.GameBarcodeDto;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {
        GameBarcodeMapperImpl.class,
})
class GameBarcodeMapperTest {

    @Autowired
    private GameBarcodeMapper gameBarcodeMapper;

    @Test
    void fromGameBarcode_withNull_returnsNull() {
        // Act
        GameBarcodeDto result = gameBarcodeMapper.fromGameBarcode(null);

        // Assert
        Assertions.assertNull(result, "The result should be null if the argument passed in is null.");
    }

    @Test
    void fromGameBarcode_withGameBarcode_mapsFields() {
        // Arrange
        GameBarcode gameBarcode = new GameBarcode();
        gameBarcode.setId(5L);
        gameBarcode.setGameId(6L);
        gameBarcode.setPlatformId(7L);
        gameBarcode.setBarcode("test-barcode");
        gameBarcode.setBarcodeType(BarcodeType.UPC_A);
        gameBarcode.setVersion(8L);

        // Act
        GameBarcodeDto result = gameBarcodeMapper.fromGameBarcode(gameBarcode);

        // Assert
        Assertions.assertEquals(gameBarcode.getId(), result.getId(), "The mapped ID does not match the entity.");
        Assertions.assertEquals(gameBarcode.getGameId(), result.getGameId(), "The mapped game ID does not match the entity.");
        Assertions.assertEquals(gameBarcode.getPlatformId(), result.getPlatformId(), "The mapped platform ID does not match the entity.");
        Assertions.assertEquals(gameBarcode.getBarcode(), result.getBarcode(), "The mapped barcode does not match the entity.");
        Assertions.assertEquals(gameBarcode.getBarcodeType(), result.getBarcodeType(), "The mapped barcode type does not match the entity.");
        Assertions.assertEquals(gameBarcode.getVersion(), result.getVersion(), "The mapped version does not match the entity.");
    }
}

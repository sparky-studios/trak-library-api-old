package com.sparkystudios.traklibrary.game.domain.converter;

import com.sparkystudios.traklibrary.game.domain.GameRegion;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class GameRegionAttributeConverterTest {

    @Test
    void convertToDatabaseColumn_withNullGameRegion_returnsGameRegionNorthAmericaId() {
        // Act
        Short result = new GameRegionAttributeConverter().convertToDatabaseColumn(null);

        // Assert
        Assertions.assertEquals(GameRegion.NORTH_AMERICA.getId(), result, "If null is provided, it should default to the id of NORTH_AMERICA.");
    }

    @Test
    void convertToDatabaseColumn_withValidGameRegion_returnsIdOfGameRegion() {
        // Act
        Short result = new GameRegionAttributeConverter().convertToDatabaseColumn(GameRegion.PAL);

        // Assert
        Assertions.assertEquals(GameRegion.PAL.getId(), result, "The id should match the GameRegion provided.");
    }

    @Test
    void convertToEntityAttribute_withNullId_returnsGameRegionNorthAmerica() {
        // Act
        GameRegion result = new GameRegionAttributeConverter().convertToEntityAttribute(null);

        // Assert
        Assertions.assertEquals(GameRegion.NORTH_AMERICA, result, "If null is provided, it should default to NORTH_AMERICA.");
    }

    @Test
    void convertToEntityAttribute_withInvalidId_throwsIllegalArgumentException() {
        // Arrange
        GameRegionAttributeConverter converter = new GameRegionAttributeConverter();

        // Assert
        Assertions.assertThrows(IllegalArgumentException.class, () ->converter.convertToEntityAttribute((short)1000));
    }

    @Test
    void convertToEntityAttribute_withGameRegionId_returnsCorrectGameRegion() {
        // Act
        GameRegion result = new GameRegionAttributeConverter().convertToEntityAttribute(GameRegion.PAL.getId());

        // Assert
        Assertions.assertEquals(GameRegion.PAL, result, "The BarcodeType should match the id provided.");
    }
}

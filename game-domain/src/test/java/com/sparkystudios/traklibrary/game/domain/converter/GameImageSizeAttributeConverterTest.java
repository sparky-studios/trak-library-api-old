package com.sparkystudios.traklibrary.game.domain.converter;

import com.sparkystudios.traklibrary.game.domain.GameImageSize;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class GameImageSizeAttributeConverterTest {

    @Test
    void convertToDatabaseColumn_withNullGameImageSize_returnsSmallId() {
        // Act
        Short result = new GameImageSizeAttributeConverter().convertToDatabaseColumn(null);

        // Assert
        Assertions.assertEquals(GameImageSize.SMALL.getId(), result, "If null is provided, it should default to the id of SMALL.");
    }

    @Test
    void convertToDatabaseColumn_withValidGameImageSize_returnsIdOfGameImageSize() {
        // Act
        Short result = new GameImageSizeAttributeConverter().convertToDatabaseColumn(GameImageSize.LARGE);

        // Assert
        Assertions.assertEquals(GameImageSize.LARGE.getId(), result, "The id should match the GameImageSize provided.");
    }

    @Test
    void convertToEntityAttribute_withNullId_returnsAgeRatingPending() {
        // Act
        GameImageSize result = new GameImageSizeAttributeConverter().convertToEntityAttribute(null);

        // Assert
        Assertions.assertEquals(GameImageSize.SMALL, result, "If null is provided, it should default to SMALL.");
    }

    @Test
    void convertToEntityAttribute_withInvalidId_throwsIllegalArgumentException() {
        // Arrange
        GameImageSizeAttributeConverter converter = new GameImageSizeAttributeConverter();

        // Assert
        Assertions.assertThrows(IllegalArgumentException.class, () -> converter.convertToEntityAttribute((short)1000));
    }

    @Test
    void convertToEntityAttribute_withValidGameSizeImageId_returnsCorrectAgeRating() {
        // Act
        GameImageSize result = new GameImageSizeAttributeConverter().convertToEntityAttribute(GameImageSize.MEDIUM.getId());

        // Assert
        Assertions.assertEquals(GameImageSize.MEDIUM, result, "The GameImageSize should match the id provided.");
    }
}

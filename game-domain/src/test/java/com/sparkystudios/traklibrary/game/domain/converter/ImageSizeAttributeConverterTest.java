package com.sparkystudios.traklibrary.game.domain.converter;

import com.sparkystudios.traklibrary.game.domain.ImageSize;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class ImageSizeAttributeConverterTest {

    @Test
    void convertToDatabaseColumn_withNullGameImageSize_returnsSmallId() {
        // Act
        Short result = new GameImageSizeAttributeConverter().convertToDatabaseColumn(null);

        // Assert
        Assertions.assertEquals(ImageSize.SMALL.getId(), result, "If null is provided, it should default to the id of SMALL.");
    }

    @Test
    void convertToDatabaseColumn_withValidGameImageSize_returnsIdOfGameImageSize() {
        // Act
        Short result = new GameImageSizeAttributeConverter().convertToDatabaseColumn(ImageSize.LARGE);

        // Assert
        Assertions.assertEquals(ImageSize.LARGE.getId(), result, "The id should match the GameImageSize provided.");
    }

    @Test
    void convertToEntityAttribute_withNullId_returnsAgeRatingPending() {
        // Act
        ImageSize result = new GameImageSizeAttributeConverter().convertToEntityAttribute(null);

        // Assert
        Assertions.assertEquals(ImageSize.SMALL, result, "If null is provided, it should default to SMALL.");
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
        ImageSize result = new GameImageSizeAttributeConverter().convertToEntityAttribute(ImageSize.MEDIUM.getId());

        // Assert
        Assertions.assertEquals(ImageSize.MEDIUM, result, "The GameImageSize should match the id provided.");
    }
}

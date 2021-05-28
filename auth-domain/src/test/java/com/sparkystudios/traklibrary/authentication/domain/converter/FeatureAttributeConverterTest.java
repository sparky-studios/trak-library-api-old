package com.sparkystudios.traklibrary.authentication.domain.converter;

import com.sparkystudios.traklibrary.authentication.domain.Feature;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class FeatureAttributeConverterTest {

    @Test
    void convertToDatabaseColumn_withNullFeature_throwsIllegalArgumentException() {
        // Act
        FeatureAttributeConverter converter = new FeatureAttributeConverter();

        // Assert
        Assertions.assertThrows(IllegalArgumentException.class, () -> converter.convertToDatabaseColumn(null));
    }

    @Test
    void convertToDatabaseColumn_withValidAuthorityType_returnsIdOfAuthorityType() {
        // Act
        Short result = new FeatureAttributeConverter().convertToDatabaseColumn(Feature.GAMES);

        // Assert
        Assertions.assertEquals(Feature.GAMES.getId(), result, "The id should match the Feature provided.");
    }

    @Test
    void convertToEntityAttribute_withNullId_throwsIllegalArgumentException() {
        // Act
        FeatureAttributeConverter converter = new FeatureAttributeConverter();

        // Assert
        Assertions.assertThrows(IllegalArgumentException.class, () -> converter.convertToEntityAttribute(null));
    }

    @Test
    void convertToEntityAttribute_withInvalidId_throwsIllegalArgumentException() {
        // Arrange
        FeatureAttributeConverter converter = new FeatureAttributeConverter();

        // Assert
        Assertions.assertThrows(IllegalArgumentException.class, () -> converter.convertToEntityAttribute((short)1000));
    }

    @Test
    void convertToEntityAttribute_withValidAuthorityTypeId_returnsCorrectAuthorityType() {
        // Act
        Feature result = new FeatureAttributeConverter().convertToEntityAttribute(Feature.GAMES.getId());

        // Assert
        Assertions.assertEquals(Feature.GAMES, result, "The Feature should match the id provided.");
    }
}

package com.sparkystudios.traklibrary.game.domain.converter;

import com.sparkystudios.traklibrary.game.domain.AgeRatingClassification;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class AgeRatingClassificationAttributeConverterTest {

    @Test
    void convertToDatabaseColumn_withNullAgeRatingClassification_returnsAgeRatingClassificationESRBId() {
        // Act
        Short result = new AgeRatingClassificationAttributeConverter().convertToDatabaseColumn(null);

        // Assert
        Assertions.assertEquals(AgeRatingClassification.ESRB.getId(), result, "If null is provided, it should default to the id of ESRB.");
    }

    @Test
    void convertToDatabaseColumn_withValidAgeRatingClassification_returnsIdOfAgeRatingClassification() {
        // Act
        Short result = new AgeRatingClassificationAttributeConverter().convertToDatabaseColumn(AgeRatingClassification.CERO);

        // Assert
        Assertions.assertEquals(AgeRatingClassification.CERO.getId(), result, "The id should match the AgeRatingClassification provided.");
    }

    @Test
    void convertToEntityAttribute_withNullId_returnsAgeRatingClassificationESRB() {
        // Act
        AgeRatingClassification result = new AgeRatingClassificationAttributeConverter().convertToEntityAttribute(null);

        // Assert
        Assertions.assertEquals(AgeRatingClassification.ESRB, result, "If null is provided, it should default to ESRB.");
    }

    @Test
    void convertToEntityAttribute_withInvalidId_throwsIllegalArgumentException() {
        // Arrange
        AgeRatingClassificationAttributeConverter converter = new AgeRatingClassificationAttributeConverter();

        // Assert
        Assertions.assertThrows(IllegalArgumentException.class, () -> converter.convertToEntityAttribute((short)1000));
    }

    @Test
    void convertToEntityAttribute_withValidAgeRatingClassificationId_returnsCorrectAgeRatingClassification() {
        // Act
        AgeRatingClassification result = new AgeRatingClassificationAttributeConverter().convertToEntityAttribute(AgeRatingClassification.CERO.getId());

        // Assert
        Assertions.assertEquals(AgeRatingClassification.CERO, result, "The AgeRatingClassification should match the id provided.");
    }
}

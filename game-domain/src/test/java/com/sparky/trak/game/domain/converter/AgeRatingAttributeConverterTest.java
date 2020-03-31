package com.sparky.trak.game.domain.converter;

import com.sparky.trak.game.domain.AgeRating;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class AgeRatingAttributeConverterTest {

    @Test
    public void convertToDatabaseColumn_withNullAgeRating_returnsRatingPendingId() {
        // Act
        Short result = new AgeRatingAttributeConverter().convertToDatabaseColumn(null);

        // Assert
        Assertions.assertEquals(AgeRating.RATING_PENDING.getId(), result, "If null is provided, it should default to the id of RATING_PENDING.");
    }

    @Test
    public void convertToDatabaseColumn_withValidAgeRating_returnsIdOfAgeRating() {
        // Act
        Short result = new AgeRatingAttributeConverter().convertToDatabaseColumn(AgeRating.TEEN);

        // Assert
        Assertions.assertEquals(AgeRating.TEEN.getId(), result, "The id should match the AgeRating provided.");
    }

    @Test
    public void convertToEntityAttribute_withNullId_returnsAgeRatingPending() {
        // Act
        AgeRating result = new AgeRatingAttributeConverter().convertToEntityAttribute(null);

        // Assert
        Assertions.assertEquals(AgeRating.RATING_PENDING, result, "If null is provided, it should default to RATING_PENDING.");
    }

    @Test
    public void convertToEntityAttribute_withInvalidId_throwsIllegalArgumentException() {
        // Assert
        Assertions.assertThrows(IllegalArgumentException.class, () -> new AgeRatingAttributeConverter().convertToEntityAttribute((short)1000));
    }

    @Test
    public void convertToEntityAttribute_withValidAgeRatingId_returnsCorrectAgeRating() {
        // Act
        AgeRating result = new AgeRatingAttributeConverter().convertToEntityAttribute(AgeRating.TEEN.getId());

        // Assert
        Assertions.assertEquals(AgeRating.TEEN, result, "The AgeRating should match the id provided.");
    }
}

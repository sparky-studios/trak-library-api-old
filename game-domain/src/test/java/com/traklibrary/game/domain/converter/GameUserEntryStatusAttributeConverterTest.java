package com.traklibrary.game.domain.converter;

import com.traklibrary.game.domain.GameUserEntryStatus;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class GameUserEntryStatusAttributeConverterTest {

    @Test
    void convertToDatabaseColumn_withNullGameUserEntryStatus_returnsGameUserEntryStatusWishlistId() {
        // Act
        Short result = new GameUserEntryStatusAttributeConverter().convertToDatabaseColumn(null);

        // Assert
        Assertions.assertEquals(GameUserEntryStatus.BACKLOG.getId(), result, "If null is provided, it should default to the id of BACKLOG.");
    }

    @Test
    void convertToDatabaseColumn_withValidGameUserEntryStatus_returnsIdOfGameUserEntryStatus() {
        // Act
        Short result = new GameUserEntryStatusAttributeConverter().convertToDatabaseColumn(GameUserEntryStatus.COMPLETED);

        // Assert
        Assertions.assertEquals(GameUserEntryStatus.COMPLETED.getId(), result, "The id should match the GameUserEntryStatus provided.");
    }

    @Test
    void convertToEntityAttribute_withNullId_returnsGameUserEntryStatusWishList() {
        // Act
        GameUserEntryStatus result = new GameUserEntryStatusAttributeConverter().convertToEntityAttribute(null);

        // Assert
        Assertions.assertEquals(GameUserEntryStatus.BACKLOG, result, "If null is provided, it should default to BACKLOG.");
    }

    @Test
    void convertToEntityAttribute_withInvalidId_throwsIllegalArgumentException() {
        // Arrange
        GameUserEntryStatusAttributeConverter converter = new GameUserEntryStatusAttributeConverter();

        // Assert
        Assertions.assertThrows(IllegalArgumentException.class, () -> converter.convertToEntityAttribute((short)1000));
    }

    @Test
    void convertToEntityAttribute_withValidGameUserEntryStatusId_returnsCorrectGameUserEntryStatus() {
        // Act
        GameUserEntryStatus result = new GameUserEntryStatusAttributeConverter().convertToEntityAttribute(GameUserEntryStatus.COMPLETED.getId());

        // Assert
        Assertions.assertEquals(GameUserEntryStatus.COMPLETED, result, "The GameUserEntryStatus should match the id provided.");
    }
}

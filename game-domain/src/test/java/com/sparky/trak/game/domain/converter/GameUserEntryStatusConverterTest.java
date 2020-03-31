package com.sparky.trak.game.domain.converter;

import com.sparky.trak.game.domain.GameUserEntryStatus;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class GameUserEntryStatusConverterTest {

    @Test
    public void convertToDatabaseColumn_withNullGameUserEntryStatus_returnsGameUserEntryStatusWishlistId() {
        // Act
        Short result = new GameUserEntryStatusConverter().convertToDatabaseColumn(null);

        // Assert
        Assertions.assertEquals(GameUserEntryStatus.WISH_LIST.getId(), result, "If null is provided, it should default to the id of WISH_LIST.");
    }

    @Test
    public void convertToDatabaseColumn_withValidGameUserEntryStatus_returnsIdOfGameUserEntryStatus() {
        // Act
        Short result = new GameUserEntryStatusConverter().convertToDatabaseColumn(GameUserEntryStatus.COMPLETED);

        // Assert
        Assertions.assertEquals(GameUserEntryStatus.COMPLETED.getId(), result, "The id should match the GameUserEntryStatus provided.");
    }

    @Test
    public void convertToEntityAttribute_withNullId_returnsGameUserEntryStatusWishList() {
        // Act
        GameUserEntryStatus result = new GameUserEntryStatusConverter().convertToEntityAttribute(null);

        // Assert
        Assertions.assertEquals(GameUserEntryStatus.WISH_LIST, result, "If null is provided, it should default to WISH_LIST.");
    }

    @Test
    public void convertToEntityAttribute_withInvalidId_throwsIllegalArgumentException() {
        // Assert
        Assertions.assertThrows(IllegalArgumentException.class, () -> new GameUserEntryStatusConverter().convertToEntityAttribute((short)1000));
    }

    @Test
    public void convertToEntityAttribute_withValidGameUserEntryStatusId_returnsCorrectGameUserEntryStatus() {
        // Act
        GameUserEntryStatus result = new GameUserEntryStatusConverter().convertToEntityAttribute(GameUserEntryStatus.COMPLETED.getId());

        // Assert
        Assertions.assertEquals(GameUserEntryStatus.COMPLETED, result, "The GameUserEntryStatus should match the id provided.");
    }
}

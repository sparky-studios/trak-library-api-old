package com.traklibrary.game.domain.converter;

import com.traklibrary.game.domain.BarcodeType;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class BarcodeTypeAttributeConverterTest {

    @Test
    void convertToDatabaseColumn_withNullBarcodeType_returnsBarcodeTypeEAN_13Id() {
        // Act
        Short result = new BarcodeTypeAttributeConverter().convertToDatabaseColumn(null);

        // Assert
        Assertions.assertEquals(BarcodeType.EAN_13.getId(), result, "If null is provided, it should default to the id of EAN_13.");
    }

    @Test
    void convertToDatabaseColumn_withValidBarcodeType_returnsIdOfBarcodeType() {
        // Act
        Short result = new BarcodeTypeAttributeConverter().convertToDatabaseColumn(BarcodeType.UPC_A);

        // Assert
        Assertions.assertEquals(BarcodeType.UPC_A.getId(), result, "The id should match the BarcodeType provided.");
    }

    @Test
    void convertToEntityAttribute_withNullId_returnsBarcodeTypePending() {
        // Act
        BarcodeType result = new BarcodeTypeAttributeConverter().convertToEntityAttribute(null);

        // Assert
        Assertions.assertEquals(BarcodeType.EAN_13, result, "If null is provided, it should default to EAN_13.");
    }

    @Test
    void convertToEntityAttribute_withInvalidId_throwsIllegalArgumentException() {
        // Assert
        Assertions.assertThrows(IllegalArgumentException.class, () -> new BarcodeTypeAttributeConverter().convertToEntityAttribute((short)1000));
    }

    @Test
    void convertToEntityAttribute_withValidBarcodeTypeId_returnsCorrectBarcodeType() {
        // Act
        BarcodeType result = new BarcodeTypeAttributeConverter().convertToEntityAttribute(BarcodeType.UPC_A.getId());

        // Assert
        Assertions.assertEquals(BarcodeType.UPC_A, result, "The BarcodeType should match the id provided.");
    }
}

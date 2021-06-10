package com.sparkystudios.traklibrary.authentication.domain.converter;

import com.sparkystudios.traklibrary.authentication.domain.AuthorityType;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class AuthorityTypeAttributeConverterTest {

    @Test
    void convertToDatabaseColumn_withNullAuthorityType_returnsAuthorityTypeREADId() {
        // Act
        Short result = new AuthorityTypeAttributeConverter().convertToDatabaseColumn(null);

        // Assert
        Assertions.assertEquals(AuthorityType.READ.getId(), result, "If null is provided, it should default to the id of READ.");
    }

    @Test
    void convertToDatabaseColumn_withValidAuthorityType_returnsIdOfAuthorityType() {
        // Act
        Short result = new AuthorityTypeAttributeConverter().convertToDatabaseColumn(AuthorityType.WRITE);

        // Assert
        Assertions.assertEquals(AuthorityType.WRITE.getId(), result, "The id should match the AuthorityType provided.");
    }

    @Test
    void convertToEntityAttribute_withNullId_returnsAuthorityTypeREAD() {
        // Act
        AuthorityType result = new AuthorityTypeAttributeConverter().convertToEntityAttribute(null);

        // Assert
        Assertions.assertEquals(AuthorityType.READ, result, "If null is provided, it should default to READ.");
    }

    @Test
    void convertToEntityAttribute_withInvalidId_throwsIllegalArgumentException() {
        // Arrange
        AuthorityTypeAttributeConverter converter = new AuthorityTypeAttributeConverter();

        // Assert
        Assertions.assertThrows(IllegalArgumentException.class, () -> converter.convertToEntityAttribute((short)1000));
    }

    @Test
    void convertToEntityAttribute_withValidAuthorityTypeId_returnsCorrectAuthorityType() {
        // Act
        AuthorityType result = new AuthorityTypeAttributeConverter().convertToEntityAttribute(AuthorityType.WRITE.getId());

        // Assert
        Assertions.assertEquals(AuthorityType.WRITE, result, "The AuthorityType should match the id provided.");
    }
}

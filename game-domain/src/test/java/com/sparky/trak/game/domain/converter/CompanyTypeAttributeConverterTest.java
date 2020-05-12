package com.sparky.trak.game.domain.converter;

import com.sparky.trak.game.domain.CompanyType;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class CompanyTypeAttributeConverterTest {

    @Test
    public void convertToDatabaseColumn_withNullCompanyType_returnsCompanyTypePublisherId() {
        // Act
        Short result = new CompanyTypeAttributeConverter().convertToDatabaseColumn(null);

        // Assert
        Assertions.assertEquals(CompanyType.PUBLISHER.getId(), result, "If null is provided, it should default to the id of PUBLISHER.");
    }

    @Test
    public void convertToDatabaseColumn_withValidCompanyType_returnsIdOfCompanyType() {
        // Act
        Short result = new CompanyTypeAttributeConverter().convertToDatabaseColumn(CompanyType.DEVELOPER);

        // Assert
        Assertions.assertEquals(CompanyType.DEVELOPER.getId(), result, "The id should match the CompanyType provided.");
    }

    @Test
    public void convertToEntityAttribute_withNullId_returnsCompanyTypePublisher() {
        // Act
        CompanyType result = new CompanyTypeAttributeConverter().convertToEntityAttribute(null);

        // Assert
        Assertions.assertEquals(CompanyType.PUBLISHER, result, "If null is provided, it should default to PUBLISHER.");
    }

    @Test
    public void convertToEntityAttribute_withInvalidId_throwsIllegalArgumentException() {
        // Assert
        Assertions.assertThrows(IllegalArgumentException.class, () -> new CompanyTypeAttributeConverter().convertToEntityAttribute((short)1000));
    }

    @Test
    public void convertToEntityAttribute_withValidCompanyTypeId_returnsCorrectCompanyType() {
        // Act
        CompanyType result = new CompanyTypeAttributeConverter().convertToEntityAttribute(CompanyType.DEVELOPER.getId());

        // Assert
        Assertions.assertEquals(CompanyType.DEVELOPER, result, "The CompanyType should match the id provided.");
    }
}

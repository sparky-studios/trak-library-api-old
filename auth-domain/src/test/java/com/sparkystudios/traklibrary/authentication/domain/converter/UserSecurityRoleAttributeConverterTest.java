package com.sparkystudios.traklibrary.authentication.domain.converter;

import com.sparkystudios.traklibrary.security.token.data.UserSecurityRole;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class UserSecurityRoleAttributeConverterTest {

    @Test
    void convertToDatabaseColumn_withNull_throwsIllegalArgumentException() {
        // Act
        UserSecurityRoleAttributeConverter converter = new UserSecurityRoleAttributeConverter();

        // Assert
        Assertions.assertThrows(NullPointerException.class, () -> converter.convertToDatabaseColumn(null));
    }

    @Test
    void convertToDatabaseColumn_withValidUserSecurityRole_returnsIdOfUserSecurityRole() {
        // Act
        String result = new UserSecurityRoleAttributeConverter().convertToDatabaseColumn(UserSecurityRole.ROLE_USER);

        // Assert
        Assertions.assertEquals(UserSecurityRole.ROLE_USER.name(), result, "The name should match the UserSecurityRole provided.");
    }

    @Test
    void convertToEntityAttribute_withNullName_throwsNullPointerException() {
        // Act
        UserSecurityRoleAttributeConverter converter = new UserSecurityRoleAttributeConverter();

        // Assert
        Assertions.assertThrows(NullPointerException.class, () -> converter.convertToEntityAttribute(null));
    }

    @Test
    void convertToEntityAttribute_withInvalidName_throwsIllegalArgumentException() {
        // Act
        UserSecurityRoleAttributeConverter converter = new UserSecurityRoleAttributeConverter();

        // Assert
        Assertions.assertThrows(IllegalArgumentException.class, () -> converter.convertToEntityAttribute("invalid"));
    }

    @Test
    void convertToEntityAttribute_withValidName_returnsCorrectUserSecurityRole() {
        // Arrange
        UserSecurityRoleAttributeConverter converter = new UserSecurityRoleAttributeConverter();

        // Act
        UserSecurityRole result = converter.convertToEntityAttribute(UserSecurityRole.ROLE_USER.name());

        // Assert
        Assertions.assertEquals(UserSecurityRole.ROLE_USER, result, "The incorrect user security role was returned.");
    }
}

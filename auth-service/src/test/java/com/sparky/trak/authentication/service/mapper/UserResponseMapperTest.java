package com.sparky.trak.authentication.service.mapper;

import com.sparky.trak.authentication.domain.User;
import com.sparky.trak.authentication.service.dto.UserResponseDto;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class UserResponseMapperTest {

    @Test
    public void userToUserCreationResponseDto_withUser_mapsFields() {
        // Arrange
        User user = new User();
        user.setId(5L);
        user.setUsername("the-test-username");
        user.setVerified(true);

        // Act
        UserResponseDto result = UserResponseMapper.INSTANCE.userToUserResponseDto(user);

        // Assert
        Assertions.assertEquals(user.getId(), result.getId(), "The mapped ID does not match the entity.");
        Assertions.assertEquals(user.getUsername(), result.getUsername(), "The mapped ID does not match the entity.");
        Assertions.assertEquals(user.isVerified(), result.isVerified(), "The mapped verified state does not match the entity.");
    }
}

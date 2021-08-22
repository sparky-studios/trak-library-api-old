package com.sparkystudios.traklibrary.authentication.service.mapper;

import com.sparkystudios.traklibrary.authentication.domain.User;
import com.sparkystudios.traklibrary.authentication.service.dto.UserResponseDto;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {
        UserResponseMapperImpl.class
})
class UserResponseMapperTest {

    @Autowired
    private UserResponseMapper userResponseMapper;

    @Test
    void fromUser_withUser_mapsFields() {
        // Arrange
        User user = new User();
        user.setId(5L);
        user.setUsername("the-test-username");
        user.setVerified(true);

        // Act
        UserResponseDto result = userResponseMapper.fromUser(user);

        // Assert
        Assertions.assertEquals(user.getId(), result.getId(), "The mapped ID does not match the entity.");
        Assertions.assertEquals(user.getUsername(), result.getUsername(), "The mapped ID does not match the entity.");
        Assertions.assertEquals(user.isVerified(), result.isVerified(), "The mapped verified state does not match the entity.");
    }
}

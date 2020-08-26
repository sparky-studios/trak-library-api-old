package com.traklibrary.authentication.service.mapper;

import com.traklibrary.authentication.domain.User;
import com.traklibrary.authentication.domain.UserRole;
import com.traklibrary.authentication.service.dto.UserDto;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Collections;

class UserMapperTest {

    @Test
    void userToUserDto_withUser_mapsFields() {
        // Arrange
        UserRole userRole = new UserRole();
        userRole.setId(1L);
        userRole.setRole("ROLE_TEST");
        userRole.setVersion(3L);


        User user = new User();
        user.setId(5L);
        user.setUsername("username");
        user.setEmailAddress("email@address.com");
        user.setPassword("password");
        user.setVerified(true);
        user.setVerificationCode("123AB");
        user.setVerificationExpiryDate(LocalDateTime.now());
        user.setVersion(1L);
        user.addUserRole(userRole);

        // Act
        UserDto result = AuthMappers.USER_MAPPER.userToUserDto(user);

        // Assert
        Assertions.assertEquals(user.getId(), result.getId(), "The mapped ID does not match the entity.");
        Assertions.assertEquals(user.getUsername(), result.getUsername(), "The mapped username does not match the entity.");
        Assertions.assertEquals(user.getEmailAddress(), result.getEmailAddress(), "The mapped email address does not match the entity.");
        Assertions.assertEquals(user.getPassword(), result.getPassword(), "The mapped password does not match the entity.");
        Assertions.assertEquals(user.isVerified(), result.isVerified(), "The mapped verified state does not match the entity.");
        Assertions.assertEquals(user.getVerificationCode(), result.getVerificationCode(), "The mapped verification code state does not match the entity.");
        Assertions.assertEquals(user.getVersion(), result.getVersion(), "The mapped version does not match the entity.");
        Assertions.assertEquals(user.getUserRoles().iterator().next().getRole(),
                result.getAuthorities().iterator().next().getAuthority(), "The mapped authority does not match the entity.");
    }

    @Test
    void userDtoToUser_withUserDto_mapsFields() {
        // Arrange
        UserDto userDto = new UserDto();
        userDto.setId(5L);
        userDto.setEmailAddress("email@address.com");
        userDto.setUsername("username");
        userDto.setPassword("password");
        userDto.setVerified(true);
        userDto.setVerificationCode("123AB");
        userDto.setVerificationExpiryDate(LocalDateTime.now());
        userDto.setVersion(1L);

        // Act
        User result = AuthMappers.USER_MAPPER.userDtoToUser(userDto);

        // Assert
        Assertions.assertEquals(userDto.getId(), result.getId(), "The mapped ID does not match the DTO.");
        Assertions.assertEquals(userDto.getEmailAddress(), result.getEmailAddress(), "The mapped email address does not match the DTO.");
        Assertions.assertEquals(userDto.getUsername(), result.getUsername(), "The mapped username does not match the DTO.");
        Assertions.assertEquals(userDto.getPassword(), result.getPassword(), "The mapped password does not match the DTO.");
        Assertions.assertEquals(userDto.isVerified(), result.isVerified(), "The mapped verified state does not match the DTO.");
        Assertions.assertEquals(userDto.getVerificationCode(), result.getVerificationCode(), "The mapped verification code state does not match the DTO.");
        Assertions.assertEquals(userDto.getVersion(), result.getVersion(), "The mapped version does not match the DTO.");
    }
}

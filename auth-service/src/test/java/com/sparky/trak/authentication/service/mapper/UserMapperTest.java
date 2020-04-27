package com.sparky.trak.authentication.service.mapper;

import com.sparky.trak.authentication.domain.User;
import com.sparky.trak.authentication.domain.UserRole;
import com.sparky.trak.authentication.domain.UserRoleXref;
import com.sparky.trak.authentication.service.dto.UserDto;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Collections;

public class UserMapperTest {

    @Test
    public void userToUserDto_withUser_mapsFields() {
        // Arrange
        UserRole userRole = new UserRole();
        userRole.setId(1L);
        userRole.setRole("ROLE_TEST");
        userRole.setVersion(3L);

        UserRoleXref userRoleXref = new UserRoleXref();
        userRoleXref.setUserRole(userRole);

        User user = new User();
        user.setId(5L);
        user.setUsername("username");
        user.setEmailAddress("email@address.com");
        user.setPassword("password");
        user.setVerified(true);
        user.setVerificationCode((short)245);
        user.setVersion(1L);
        user.setUserRoleXrefs(Collections.singleton(userRoleXref));

        // Act
        UserDto result = UserMapper.INSTANCE.userToUserDto(user);

        // Assert
        Assertions.assertEquals(user.getId(), result.getId(), "The mapped ID does not match the entity.");
        Assertions.assertEquals(user.getUsername(), result.getUsername(), "The mapped username does not match the entity.");
        Assertions.assertEquals(user.getEmailAddress(), result.getEmailAddress(), "The mapped email address does not match the entity.");
        Assertions.assertEquals(user.getPassword(), result.getPassword(), "The mapped password does not match the entity.");
        Assertions.assertEquals(user.isVerified(), result.isVerified(), "The mapped verified state does not match the entity.");
        Assertions.assertEquals(user.getVerificationCode(), result.getVerificationCode(), "The mapped verification code state does not match the entity.");
        Assertions.assertEquals(user.getVersion(), result.getVersion(), "The mapped version does not match the entity.");
        Assertions.assertEquals(user.getUserRoleXrefs().iterator().next().getUserRole().getRole(),
                result.getAuthorities().iterator().next().getAuthority(), "The mapped authority does not match the entity.");
    }

    @Test
    public void userDtoToUser_withUserDto_mapsFields() {
        // Arrange
        UserDto userDto = new UserDto();
        userDto.setId(5L);
        userDto.setEmailAddress("email@address.com");
        userDto.setUsername("username");
        userDto.setPassword("password");
        userDto.setVerified(true);
        userDto.setVerificationCode((short)3894);
        userDto.setVersion(1L);

        // Act
        User result = UserMapper.INSTANCE.userDtoToUser(userDto);

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

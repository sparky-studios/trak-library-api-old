package com.sparkystudios.traklibrary.authentication.service.mapper;

import com.sparkystudios.traklibrary.authentication.domain.User;
import com.sparkystudios.traklibrary.authentication.domain.UserRole;
import com.sparkystudios.traklibrary.authentication.service.dto.UserDto;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

class UserMapperTest {

    @Test
    void userToUserDto_withNull_returnsNull() {
        // Act
        UserDto result = AuthMappers.USER_MAPPER.userToUserDto(null);

        // Assert
        Assertions.assertThat(result).isNull();
    }

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
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());
        user.setVersion(1L);
        user.addUserRole(userRole);

        // Act
        UserDto result = AuthMappers.USER_MAPPER.userToUserDto(user);

        // Assert
        Assertions.assertThat(result.getId()).isEqualTo(user.getId());
        Assertions.assertThat(result.getUsername()).isEqualTo(user.getUsername());
        Assertions.assertThat(result.getEmailAddress()).isEqualTo(user.getEmailAddress());
        Assertions.assertThat(result.getPassword()).isEqualTo(user.getPassword());
        Assertions.assertThat(result.isVerified()).isEqualTo(user.isVerified());
        Assertions.assertThat(result.getVerificationCode()).isEqualTo(user.getVerificationCode());
        Assertions.assertThat(result.getVerificationExpiryDate()).isEqualTo(user.getVerificationExpiryDate());
        Assertions.assertThat(result.getCreatedAt()).isEqualTo(user.getCreatedAt());
        Assertions.assertThat(result.getUpdatedAt()).isEqualTo(user.getUpdatedAt());
        Assertions.assertThat(result.getVersion()).isEqualTo(user.getVersion());
        Assertions.assertThat(result.getAuthorities().iterator().next().getAuthority()).isEqualTo(user.getUserRoles().iterator().next().getRole());
    }

    @Test
    void userDtoToUser_withNull_returnsNull() {
        // Act
        User result = AuthMappers.USER_MAPPER.userDtoToUser(null);

        // Assert
        Assertions.assertThat(result).isNull();
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
        userDto.setCreatedAt(LocalDateTime.now());
        userDto.setUpdatedAt(LocalDateTime.now());
        userDto.setVersion(1L);

        // Act
        User result = AuthMappers.USER_MAPPER.userDtoToUser(userDto);

        // Assert
        Assertions.assertThat(result.getId()).isEqualTo(userDto.getId());
        Assertions.assertThat(result.getUsername()).isEqualTo(userDto.getUsername());
        Assertions.assertThat(result.getEmailAddress()).isEqualTo(userDto.getEmailAddress());
        Assertions.assertThat(result.getPassword()).isEqualTo(userDto.getPassword());
        Assertions.assertThat(result.isVerified()).isEqualTo(userDto.isVerified());
        Assertions.assertThat(result.getVerificationCode()).isEqualTo(userDto.getVerificationCode());
        Assertions.assertThat(result.getVerificationExpiryDate()).isEqualTo(userDto.getVerificationExpiryDate());
        Assertions.assertThat(result.getCreatedAt()).isNull();
        Assertions.assertThat(result.getUpdatedAt()).isNull();
        Assertions.assertThat(result.getVersion()).isEqualTo(userDto.getVersion());
    }
}

package com.sparkystudios.traklibrary.authentication.service.mapper;

import com.sparkystudios.traklibrary.authentication.domain.User;
import com.sparkystudios.traklibrary.authentication.domain.UserRole;
import com.sparkystudios.traklibrary.authentication.service.dto.UserDto;
import com.sparkystudios.traklibrary.security.token.data.UserSecurityRole;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDateTime;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {
        UserMapperImpl.class
})
class UserMapperTest {

    @Autowired
    private UserMapper userMapper;

    @Test
    void fromUser_withNull_returnsNull() {
        // Act
        UserDto result = userMapper.fromUser(null);

        // Assert
        Assertions.assertThat(result).isNull();
    }

    @Test
    void fromUser_withUser_mapsFields() {
        // Arrange
        UserRole userRole = new UserRole();
        userRole.setId(1L);
        userRole.setRole(UserSecurityRole.ROLE_USER);
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
        user.setUserRole(userRole);

        // Act
        UserDto result = userMapper.fromUser(user);

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
        Assertions.assertThat(result.getAuthorities().iterator().next().getAuthority())
                .isEqualTo(userRole.getRole().name());
    }

    @Test
    void toUser_withNull_returnsNull() {
        // Act
        User result = userMapper.toUser(null);

        // Assert
        Assertions.assertThat(result).isNull();
    }

    @Test
    void toUser_withUserDto_mapsFields() {
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
        User result = userMapper.toUser(userDto);

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

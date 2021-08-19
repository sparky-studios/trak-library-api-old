package com.sparkystudios.traklibrary.authentication.service.impl;

import com.sparkystudios.traklibrary.authentication.service.UserService;
import com.sparkystudios.traklibrary.authentication.service.dto.RegistrationResponseDto;
import com.sparkystudios.traklibrary.authentication.service.dto.TwoFactorAuthenticationRequestDto;
import com.sparkystudios.traklibrary.authentication.service.dto.UserDto;
import com.sparkystudios.traklibrary.authentication.service.mapper.UserMapper;
import com.sparkystudios.traklibrary.authentication.service.mapper.UserResponseMapper;
import dev.samstevens.totp.code.CodeVerifier;
import dev.samstevens.totp.exceptions.QrGenerationException;
import dev.samstevens.totp.qr.ZxingPngQrGenerator;
import dev.samstevens.totp.secret.SecretGenerator;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.MessageSource;
import org.springframework.security.authentication.BadCredentialsException;

import javax.persistence.EntityExistsException;
import java.util.Locale;

@ExtendWith(MockitoExtension.class)
class TwoFactorAuthenticationServiceImplTest {

    @Mock
    private UserService userService;

    @Mock
    private SecretGenerator secretGenerator;

    @Mock
    private ZxingPngQrGenerator zxingPngQrGenerator;

    @Mock
    private CodeVerifier codeVerifier;

    @Mock
    private MessageSource messageSource;

    @Spy
    private UserResponseMapper userResponseMapper;

    @Spy
    private UserMapper userMapper;

    @InjectMocks
    private TwoFactorAuthenticationServiceImpl twoFactorAuthenticationService;

    @Test
    void createTwoFactorAuthenticationSecret_withUserWithEnabledTwoFactorAuthentication_throwsEntityExistsException() {
        // Arrange
        UserDto userDto = new UserDto();
        userDto.setUsingTwoFactorAuthentication(true);

        Mockito.when(userService.findById(ArgumentMatchers.anyLong()))
                .thenReturn(userDto);

        Mockito.when(messageSource.getMessage(
                ArgumentMatchers.eq("two-factor-authentication.exception.exists"), ArgumentMatchers.any(Object[].class), ArgumentMatchers.any(Locale.class)))
                .thenReturn("");

        // Assert
        Assertions.assertThatThrownBy(() -> twoFactorAuthenticationService.createTwoFactorAuthenticationSecret(1L))
                .isInstanceOf(EntityExistsException.class);
    }

    @Test
    void createTwoFactorAuthenticationSecret_withUserWithInvalidQrCodeGeneration_throwsIllegalStateException() throws QrGenerationException {
        // Arrange
        UserDto userDto = new UserDto();
        userDto.setUsername("username");

        Mockito.when(userService.findById(ArgumentMatchers.anyLong()))
                .thenReturn(userDto);

        Mockito.when(secretGenerator.generate())
                .thenReturn("123");

        Mockito.when(zxingPngQrGenerator.generate(ArgumentMatchers.any()))
                        .thenThrow(QrGenerationException.class);

        Mockito.when(messageSource.getMessage(
                ArgumentMatchers.eq("two-factor-authentication.error.invalid-qr-code"), ArgumentMatchers.any(Object[].class), ArgumentMatchers.any(Locale.class)))
                .thenReturn("");

        // Assert
        Assertions.assertThatThrownBy(() -> twoFactorAuthenticationService.createTwoFactorAuthenticationSecret(1L))
                .isInstanceOf(IllegalStateException.class);
    }

    @Test
    void createTwoFactorAuthenticationSecret_withUserWithValidQrCodeGeneration_returnsValidResponse() throws QrGenerationException {
        // Arrange
        UserDto userDto = new UserDto();
        userDto.setId(1L);
        userDto.setUsername("username");

        Mockito.when(userService.findById(ArgumentMatchers.anyLong()))
                .thenReturn(userDto);

        Mockito.when(secretGenerator.generate())
                .thenReturn("123");

        Mockito.when(zxingPngQrGenerator.generate(ArgumentMatchers.any()))
                    .thenReturn(new byte[] {});

        // Act
        RegistrationResponseDto result = twoFactorAuthenticationService.createTwoFactorAuthenticationSecret(1L);

        // Assert
        Assertions.assertThat(result.getUserId()).isEqualTo(userDto.getId());
        Assertions.assertThat(result.getQrData()).isNotNull();
    }

    @Test
    void enable_withUserAlreadyUsingTwoFactorAuthentication_returnsUserResponse() {
        // Arrange
        UserDto userDto = new UserDto();
        userDto.setUsingTwoFactorAuthentication(true);

        Mockito.when(userService.findById(ArgumentMatchers.anyLong()))
                .thenReturn(userDto);

        // Act
        twoFactorAuthenticationService.enable(1L, new TwoFactorAuthenticationRequestDto());

        // Assert
        Mockito.verify(userResponseMapper, Mockito.atMostOnce())
                .fromUser(ArgumentMatchers.any());

        Mockito.verify(userMapper, Mockito.atMostOnce())
                .toUser(ArgumentMatchers.any());
    }

    @Test
    void enable_withInvalidCode_throwsBadCredentialsException() {
        // Arrange
        UserDto userDto = new UserDto();
        userDto.setTwoFactorAuthenticationSecret("123");

        Mockito.when(userService.findById(ArgumentMatchers.anyLong()))
                .thenReturn(userDto);

        Mockito.when(codeVerifier.isValidCode(ArgumentMatchers.anyString(), ArgumentMatchers.anyString()))
                .thenReturn(false);

        Mockito.when(messageSource.getMessage(
                ArgumentMatchers.eq("authentication.exception.bad-2fa-credentials"), ArgumentMatchers.any(Object[].class), ArgumentMatchers.any(Locale.class)))
                .thenReturn("");

        TwoFactorAuthenticationRequestDto twoFactorAuthenticationRequestDto = new TwoFactorAuthenticationRequestDto();
        twoFactorAuthenticationRequestDto.setCode("321");

        // Assert
        Assertions.assertThatThrownBy(() -> twoFactorAuthenticationService.enable(1L, twoFactorAuthenticationRequestDto))
                .isInstanceOf(BadCredentialsException.class);
    }

    @Test
    void enable_withValidCode_setTwoFactorAuthenticationAndUpdatesUser() {
        // Arrange
        UserDto userDto = Mockito.spy(UserDto.class);
        userDto.setTwoFactorAuthenticationSecret("123");

        Mockito.when(userService.findById(ArgumentMatchers.anyLong()))
                .thenReturn(userDto);

        Mockito.when(codeVerifier.isValidCode(ArgumentMatchers.anyString(), ArgumentMatchers.anyString()))
                .thenReturn(true);

        Mockito.when(userService.update(ArgumentMatchers.any(UserDto.class)))
                .thenReturn(new UserDto());

        TwoFactorAuthenticationRequestDto twoFactorAuthenticationRequestDto = new TwoFactorAuthenticationRequestDto();
        twoFactorAuthenticationRequestDto.setCode("321");

        // Act
        twoFactorAuthenticationService.enable(1L, twoFactorAuthenticationRequestDto);

        // Assert
        Mockito.verify(userDto, Mockito.atMostOnce())
                .setUsingTwoFactorAuthentication(true);

        Mockito.verify(userResponseMapper, Mockito.atMostOnce())
                .fromUser(ArgumentMatchers.any());

        Mockito.verify(userMapper, Mockito.atMostOnce())
                .toUser(ArgumentMatchers.any());
    }

    @Test
    void disable_withValidUserDto_disablesTwoFactorAuthentication() {
        // Arrange
        UserDto userDto = Mockito.spy(UserDto.class);

        Mockito.when(userService.findById(ArgumentMatchers.anyLong()))
                .thenReturn(userDto);

        Mockito.when(userService.update(ArgumentMatchers.any(UserDto.class)))
                .thenReturn(new UserDto());

        // Act
        twoFactorAuthenticationService.disable(1L);

        // Assert
        Mockito.verify(userDto, Mockito.atMostOnce())
                .setTwoFactorAuthenticationSecret(null);

        Mockito.verify(userDto, Mockito.atMostOnce())
                .setUsingTwoFactorAuthentication(false);

        Mockito.verify(userResponseMapper, Mockito.atMostOnce())
                .fromUser(ArgumentMatchers.any());

        Mockito.verify(userMapper, Mockito.atMostOnce())
                .toUser(ArgumentMatchers.any());
    }
}

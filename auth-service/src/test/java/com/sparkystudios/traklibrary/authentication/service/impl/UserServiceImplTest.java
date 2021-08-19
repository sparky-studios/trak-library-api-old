package com.sparkystudios.traklibrary.authentication.service.impl;

import com.sparkystudios.traklibrary.authentication.domain.User;
import com.sparkystudios.traklibrary.authentication.domain.UserRole;
import com.sparkystudios.traklibrary.authentication.repository.UserRepository;
import com.sparkystudios.traklibrary.authentication.repository.UserRoleRepository;
import com.sparkystudios.traklibrary.authentication.service.dto.*;
import com.sparkystudios.traklibrary.authentication.service.event.RecoveryEvent;
import com.sparkystudios.traklibrary.authentication.service.event.VerificationEvent;
import com.sparkystudios.traklibrary.authentication.service.exception.InvalidUserException;
import com.sparkystudios.traklibrary.authentication.service.mapper.UserMapper;
import com.sparkystudios.traklibrary.authentication.service.mapper.UserResponseMapper;
import com.sparkystudios.traklibrary.security.AuthenticationService;
import com.sparkystudios.traklibrary.security.token.data.UserSecurityRole;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.context.MessageSource;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.persistence.EntityNotFoundException;
import java.util.Locale;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserRoleRepository userRoleRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Spy
    private UserMapper userMapper;

    @Spy
    private UserResponseMapper userResponseMapper;

    @Mock
    private MessageSource messageSource;

    @Mock
    private StreamBridge streamBridge;

    @Mock
    private AuthenticationService authenticationService;

    @InjectMocks
    private UserServiceImpl userService;

    @Test
    void loadUserByUsername_withNonExistentUser_throwsUsernameNotFoundException() {
        // Arrange
        Mockito.when(userRepository.findByUsername(ArgumentMatchers.anyString()))
                .thenReturn(Optional.empty());

        // Assert
        Assertions.assertThatThrownBy(() -> userService.loadUserByUsername(""))
                        .isInstanceOf(UsernameNotFoundException.class);
    }

    @Test
    void loadUserByUsername_withExistingUser_returnsMappedUserDto() {
        // Arrange
        Mockito.when(userRepository.findByUsername(ArgumentMatchers.anyString()))
                .thenReturn(Optional.of(new User()));

        // Act
        userService.loadUserByUsername("");

        // Assert
        Mockito.verify(userMapper, Mockito.atMostOnce())
                .fromUser(ArgumentMatchers.any());
    }

    @Test
    void save_withExistingUsername_throwsEntityExistsException() {
        // Arrange
        Mockito.when(userRepository.findByUsername(ArgumentMatchers.anyString()))
                .thenReturn(Optional.of(new User()));

        Mockito.when(messageSource.getMessage(ArgumentMatchers.anyString(), ArgumentMatchers.any(Object[].class), ArgumentMatchers.any(Locale.class)))
                .thenReturn("username-error");

        RegistrationRequestDto registrationRequestDto = new RegistrationRequestDto();
        registrationRequestDto.setUsername("username");
        registrationRequestDto.setEmailAddress("email.address@email.com");
        registrationRequestDto.setPassword("password");

        // Act
        CheckedResponse<RegistrationResponseDto> result = userService.save(registrationRequestDto);

        // Assert
        Assertions.assertThat(result.getData())
                .isNull();
        Assertions.assertThat(result.getErrorMessage())
                .isEqualTo("username-error");
        Assertions.assertThat(result.isError())
                .isTrue();

        Mockito.verify(userRepository, Mockito.never())
                .save(ArgumentMatchers.any());
    }

    @Test
    void save_withExistingEmailAddress_throwsEntityExistsException() {
        // Arrange
        Mockito.when(userRepository.findByUsername(ArgumentMatchers.anyString()))
                .thenReturn(Optional.empty());

        Mockito.when(userRepository.findByEmailAddress(ArgumentMatchers.anyString()))
                .thenReturn(Optional.of(new User()));

        Mockito.when(messageSource.getMessage(ArgumentMatchers.anyString(), ArgumentMatchers.any(Object[].class), ArgumentMatchers.any(Locale.class)))
                .thenReturn("email-error");

        RegistrationRequestDto registrationRequestDto = new RegistrationRequestDto();
        registrationRequestDto.setUsername("username");
        registrationRequestDto.setEmailAddress("email.address@email.com");
        registrationRequestDto.setPassword("password");

        // Act
        CheckedResponse<RegistrationResponseDto> result = userService.save(registrationRequestDto);

        // Assert
        Assertions.assertThat(result.getData())
                .isNull();
        Assertions.assertThat(result.getErrorMessage())
                .isEqualTo("email-error");
        Assertions.assertThat(result.isError())
                .isTrue();

        Mockito.verify(userRepository, Mockito.never())
                .save(ArgumentMatchers.any());
    }

    @Test
    void save_withMissingUserRole_throwsEntityNotFoundException() {
        // Arrange
        Mockito.when(userRepository.findByUsername(ArgumentMatchers.anyString()))
                .thenReturn(Optional.empty());

        Mockito.when(userRepository.findByEmailAddress(ArgumentMatchers.anyString()))
                .thenReturn(Optional.empty());

        Mockito.when(userRoleRepository.findByRole((UserSecurityRole.ROLE_USER)))
                .thenReturn(Optional.empty());

        Mockito.when(messageSource.getMessage(ArgumentMatchers.anyString(), ArgumentMatchers.any(Object[].class), ArgumentMatchers.any(Locale.class)))
                .thenReturn("");

        RegistrationRequestDto registrationRequestDto = new RegistrationRequestDto();
        registrationRequestDto.setUsername("username");
        registrationRequestDto.setEmailAddress("email.address@email.com");
        registrationRequestDto.setPassword("password");

        // Assert
        Assertions.assertThatThrownBy(() -> userService.save(registrationRequestDto))
                .isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    void save_withValidCredentialsAndUserRole_savesUserAndMakesUserRoleXrefAndPublishesEvent() {
        // Arrange
        Mockito.when(userRepository.findByUsername(ArgumentMatchers.anyString()))
                .thenReturn(Optional.empty());

        Mockito.when(userRepository.findByEmailAddress(ArgumentMatchers.anyString()))
                .thenReturn(Optional.empty());

        Mockito.when(userRoleRepository.findByRole(UserSecurityRole.ROLE_USER))
                .thenReturn(Optional.of(new UserRole()));

        Mockito.when(streamBridge.send(ArgumentMatchers.anyString(), ArgumentMatchers.any()))
                .thenReturn(true);

        User user = new User();
        user.setEmailAddress("random-address@trak.com");

        Mockito.when(userRepository.save(ArgumentMatchers.any()))
                .thenReturn(user);

        Mockito.when(passwordEncoder.encode(ArgumentMatchers.anyString()))
                .thenReturn("password");

        RegistrationRequestDto registrationRequestDto = new RegistrationRequestDto();
        registrationRequestDto.setUsername("username");
        registrationRequestDto.setEmailAddress("email.address@email.com");
        registrationRequestDto.setPassword("password");

        // Act
        CheckedResponse<RegistrationResponseDto> result = userService.save(registrationRequestDto);

        // Assert
        Assertions.assertThat(result).isNotNull();
        Assertions.assertThat(result.getErrorMessage()).isEmpty();
        Assertions.assertThat(result.isError()).isFalse();

        Mockito.verify(userRepository, Mockito.atMostOnce())
                .save(ArgumentMatchers.any());

        Mockito.verify(streamBridge, Mockito.atMostOnce())
                .send(ArgumentMatchers.eq("trak-email-verification"), ArgumentMatchers.any(VerificationEvent.class));
    }

    @Test
    void update_withNullUserDto_throwsNullPointerException() {
        // Arrange
        UserDto userDto = null;

        // Assert
        Assertions.assertThatThrownBy(() -> userService.update(userDto))
                .isInstanceOf(NullPointerException.class);
    }

    @Test
    void update_withNonExistentUser_throwsEntityNotFoundException() {
        // Arrange
        Mockito.when(userRepository.existsById(ArgumentMatchers.anyLong()))
                .thenReturn(false);

        Mockito.when(messageSource.getMessage(ArgumentMatchers.eq("user.exception.not-found"), ArgumentMatchers.any(Object[].class), ArgumentMatchers.any(Locale.class)))
                .thenReturn("");

        UserDto userDto = new UserDto();

        // Assert
        Assertions.assertThatThrownBy(() -> userService.update(userDto))
                .isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    void update_withUserDto_updatesUserDto() {
        // Arrange
        Mockito.when(userRepository.existsById(ArgumentMatchers.anyLong()))
                .thenReturn(true);

        UserDto userDto = new UserDto();

        Mockito.when(userRepository.save(ArgumentMatchers.any()))
                .thenReturn(new User());

        // Act
        userService.update(userDto);

        // Assert
        Mockito.verify(userRepository, Mockito.atMostOnce())
                .save(ArgumentMatchers.any());
    }

    @Test
    void update_withNonExistentUser_returnsCheckedResponseWithError() {
        // Arrange
        Mockito.when(userRepository.findByUsername(ArgumentMatchers.anyString()))
                .thenReturn(Optional.empty());

        Mockito.when(messageSource.getMessage(ArgumentMatchers.eq("user.error.non-existent-username"), ArgumentMatchers.any(Object[].class), ArgumentMatchers.any(Locale.class)))
                .thenReturn("user-error");

        RecoveryRequestDto recoveryRequestDto = new RecoveryRequestDto();
        recoveryRequestDto.setUsername("username");

        // Act
        CheckedResponse<UserResponseDto> result = userService.update(recoveryRequestDto);

        // Assert
        Assertions.assertThat(result.getData())
                .isNull();
        Assertions.assertThat(result.getErrorMessage())
                .isEqualTo("user-error");
        Assertions.assertThat(result.isError())
                .isTrue();

        Mockito.verify(userRepository, Mockito.never())
                .save(ArgumentMatchers.any());
    }

    @Test
    void update_withNullUserRecoveryToken_returnsCheckedResponseWithError() {
        // Arrange
        Mockito.when(userRepository.findByUsername(ArgumentMatchers.anyString()))
                .thenReturn(Optional.of(new User()));

        Mockito.when(messageSource.getMessage(ArgumentMatchers.eq("user.error.incorrect-recovery-token"), ArgumentMatchers.any(Object[].class), ArgumentMatchers.any(Locale.class)))
                .thenReturn("user-error");

        RecoveryRequestDto recoveryRequestDto = new RecoveryRequestDto();
        recoveryRequestDto.setUsername("username");
        recoveryRequestDto.setRecoveryToken("recovery");

        // Act
        CheckedResponse<UserResponseDto> result = userService.update(recoveryRequestDto);

        // Assert
        Assertions.assertThat(result.getData())
                .isNull();
        Assertions.assertThat(result.getErrorMessage())
                .isEqualTo("user-error");
        Assertions.assertThat(result.isError())
                .isTrue();

        Mockito.verify(userRepository, Mockito.never())
                .save(ArgumentMatchers.any());
    }

    @Test
    void update_withIncorrectRecoveryToken_returnsCheckedResponseWithError() {
        // Arrange
        User user = new User();
        user.setRecoveryToken("recovery-2");

        Mockito.when(userRepository.findByUsername(ArgumentMatchers.anyString()))
                .thenReturn(Optional.of(user));

        Mockito.when(messageSource.getMessage(ArgumentMatchers.eq("user.error.incorrect-recovery-token"), ArgumentMatchers.any(Object[].class), ArgumentMatchers.any(Locale.class)))
                .thenReturn("user-error");

        RecoveryRequestDto recoveryRequestDto = new RecoveryRequestDto();
        recoveryRequestDto.setUsername("username");
        recoveryRequestDto.setRecoveryToken("recovery");

        // Act
        CheckedResponse<UserResponseDto> result = userService.update(recoveryRequestDto);

        // Assert
        Assertions.assertThat(result.getData())
                .isNull();
        Assertions.assertThat(result.getErrorMessage())
                .isEqualTo("user-error");
        Assertions.assertThat(result.isError())
                .isTrue();

        Mockito.verify(userRepository, Mockito.never())
                .save(ArgumentMatchers.any());
    }

    @Test
    void update_withValidRecoveryToken_savesUserAndReturnsValidCheckedResponse() {
        // Arrange
        User user = new User();
        user.setRecoveryToken("recovery");

        Mockito.when(userRepository.findByUsername(ArgumentMatchers.anyString()))
                .thenReturn(Optional.of(user));

        Mockito.when(userRepository.save(ArgumentMatchers.any()))
                .thenReturn(user);

        Mockito.when(passwordEncoder.encode(ArgumentMatchers.anyString()))
                .thenReturn("password");

        Mockito.when(userResponseMapper.fromUser(ArgumentMatchers.any()))
                .thenReturn(new UserResponseDto());

        RecoveryRequestDto recoveryRequestDto = new RecoveryRequestDto();
        recoveryRequestDto.setUsername("username");
        recoveryRequestDto.setRecoveryToken("recovery");
        recoveryRequestDto.setPassword("password");

        // Act
        CheckedResponse<UserResponseDto> result = userService.update(recoveryRequestDto);

        // Assert
        Assertions.assertThat(result.getData())
                .isNotNull();
        Assertions.assertThat(result.getErrorMessage())
                .isEmpty();
        Assertions.assertThat(result.isError())
                .isFalse();

        Mockito.verify(userRepository, Mockito.atMostOnce())
                .save(ArgumentMatchers.any());
    }

    @Test
    void deleteByIde_withNoMatchingUser_throwsEntityNotFoundException() {
        // Arrange
        Mockito.when(userRepository.findById(ArgumentMatchers.anyLong()))
                .thenReturn(Optional.empty());

        // Assert
        Assertions.assertThatThrownBy(() -> userService.deleteById(1L))
                        .isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    void deleteByUsername_withDifferentUser_throwsInvalidUserException() {
        // Arrange
        Mockito.when(userRepository.findById(ArgumentMatchers.anyLong()))
                .thenReturn(Optional.of(new User()));

        Mockito.when(authenticationService.isCurrentAuthenticatedUser(ArgumentMatchers.anyLong()))
                .thenReturn(false);

        Mockito.when(messageSource.getMessage(ArgumentMatchers.anyString(), ArgumentMatchers.any(Object[].class), ArgumentMatchers.any(Locale.class)))
                .thenReturn("");

        // Assert
        Assertions.assertThatThrownBy(() -> userService.deleteById(1L))
                .isInstanceOf(InvalidUserException.class);
    }

    @Test
    void deleteByUsername_withValidUser_deletesUserRolesAndUser() {
        // Arrange
        Mockito.when(userRepository.findById(ArgumentMatchers.anyLong()))
                .thenReturn(Optional.of(new User()));

        Mockito.when(authenticationService.isCurrentAuthenticatedUser(ArgumentMatchers.anyLong()))
                .thenReturn(true);

        Mockito.doNothing()
                .when(userRepository).deleteById(ArgumentMatchers.anyLong());

        Mockito.when(userMapper.fromUser(ArgumentMatchers.any()))
                .thenReturn(new UserDto());

        // Act
        userService.deleteById(1L);

        // Assert
        Mockito.verify(userRepository, Mockito.atMostOnce())
                .deleteById(ArgumentMatchers.anyLong());
    }

    @Test
    void verify_withNoMatchingUser_throwsEntityNotFoundException() {
        // Arrange
        Mockito.when(userRepository.findById(ArgumentMatchers.anyLong()))
                .thenReturn(Optional.empty());

        // Assert
        Assertions.assertThatThrownBy(() -> userService.verify(1L, "11111"))
                        .isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    void verify_withDifferentUser_throwsInvalidUserException() {
        // Arrange
        Mockito.when(userRepository.findById(ArgumentMatchers.anyLong()))
                .thenReturn(Optional.of(new User()));

        Mockito.when(authenticationService.isCurrentAuthenticatedUser(ArgumentMatchers.anyLong()))
                .thenReturn(false);

        Mockito.when(messageSource.getMessage(ArgumentMatchers.anyString(), ArgumentMatchers.any(Object[].class), ArgumentMatchers.any(Locale.class)))
                .thenReturn("");

        // Assert
        Assertions.assertThatThrownBy(() -> userService.verify(1L, "11111"))
                .isInstanceOf(InvalidUserException.class);
    }

    @Test
    void verify_withVerifiedUser_doesntUpdateVerificationStatus() {
        // Arrange
        Mockito.when(userRepository.findById(ArgumentMatchers.anyLong()))
                .thenReturn(Optional.of(new User()));

        Mockito.when(authenticationService.isCurrentAuthenticatedUser(ArgumentMatchers.anyLong()))
                .thenReturn(true);

        UserDto userDto = new UserDto();
        userDto.setVerified(true);

        Mockito.when(userMapper.fromUser(ArgumentMatchers.any()))
                .thenReturn(userDto);

        // Act
        CheckedResponse<Boolean> result = userService.verify(1L, "11111");

        // Assert
        Assertions.assertThat(result.getData())
                .isTrue();
        Assertions.assertThat(result.isError())
                .isFalse();
        Assertions.assertThat(result.getErrorMessage())
                .isEmpty();

        Mockito.verify(userRepository, Mockito.never())
                .save(ArgumentMatchers.any());
    }

    @Test
    void verify_withNonVerifiedUserButIncorrectVerificationCode_returnsCheckedResponseWithError() {
        // Arrange
        UserDto userDto = new UserDto();
        userDto.setVerificationCode("11112");

        Mockito.when(userRepository.findById(ArgumentMatchers.anyLong()))
                .thenReturn(Optional.of(new User()));

        Mockito.when(authenticationService.isCurrentAuthenticatedUser(ArgumentMatchers.anyLong()))
                .thenReturn(true);

        Mockito.when(messageSource.getMessage(ArgumentMatchers.anyString(), ArgumentMatchers.any(Object[].class), ArgumentMatchers.any(Locale.class)))
                .thenReturn("error");

        Mockito.when(userMapper.fromUser(ArgumentMatchers.any()))
                .thenReturn(userDto);

        // Act
        CheckedResponse<Boolean> result = userService.verify(1L, "11111");

        // Assert
        Assertions.assertThat(result.getData())
                .isFalse();
        Assertions.assertThat(result.isError())
                .isTrue();
        Assertions.assertThat(result.getErrorMessage())
                .isEqualTo("error");

        Mockito.verify(userRepository, Mockito.never())
                .save(ArgumentMatchers.any());
    }

    @Test
    void verify_withNonVerifiedUserWithNullVerificationCode_returnsCheckedResponseWithError() {
        // Arrange
        Mockito.when(userRepository.findById(ArgumentMatchers.anyLong()))
                .thenReturn(Optional.of(new User()));

        Mockito.when(authenticationService.isCurrentAuthenticatedUser(ArgumentMatchers.anyLong()))
                .thenReturn(true);

        Mockito.when(messageSource.getMessage(ArgumentMatchers.anyString(), ArgumentMatchers.any(Object[].class), ArgumentMatchers.any(Locale.class)))
                .thenReturn("error");

        Mockito.when(userMapper.fromUser(ArgumentMatchers.any()))
                .thenReturn(new UserDto());

        // Act
        CheckedResponse<Boolean> result = userService.verify(1L, "11111");

        // Assert
        Assertions.assertThat(result.getData())
                .isFalse();
        Assertions.assertThat(result.isError())
                .isTrue();
        Assertions.assertThat(result.getErrorMessage())
                .isEqualTo("error");

        Mockito.verify(userRepository, Mockito.never())
                .save(ArgumentMatchers.any());
    }

    @Test
    void verify_withNonVerifiedUserWithCorrectVerificationCode_updatesUser() {
        // Arrange
        Mockito.when(userRepository.findById(ArgumentMatchers.anyLong()))
                .thenReturn(Optional.of(new User()));

        Mockito.when(authenticationService.isCurrentAuthenticatedUser(ArgumentMatchers.anyLong()))
                .thenReturn(true);

        Mockito.when(userRepository.save(ArgumentMatchers.any()))
                .thenReturn(new User());

        UserDto userDto = new UserDto();
        userDto.setVerificationCode("11111");

        Mockito.when(userMapper.fromUser(ArgumentMatchers.any()))
                .thenReturn(userDto);

        // Act
        CheckedResponse<Boolean> result = userService.verify(1L, "11111");

        // Assert
        Assertions.assertThat(result.getData())
                .isTrue();
        Assertions.assertThat(result.isError())
                .isFalse();
        Assertions.assertThat(result.getErrorMessage())
                .isEmpty();

        Mockito.verify(userRepository, Mockito.atMostOnce())
                .save(ArgumentMatchers.any());
    }

    @Test
    void reverify_withNoMatchingUser_throwsEntityNotFoundException() {
        // Arrange
        Mockito.when(userRepository.findById(ArgumentMatchers.anyLong()))
                .thenReturn(Optional.empty());

        Mockito.when(messageSource.getMessage(ArgumentMatchers.anyString(), ArgumentMatchers.any(Object[].class), ArgumentMatchers.any(Locale.class)))
                .thenReturn("");

        // Assert
        Assertions.assertThatThrownBy(() -> userService.reverify(1L))
                        .isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    void reverify_withDifferentUser_throwsInvalidUserException() {
        // Arrange
        Mockito.when(userRepository.findById(ArgumentMatchers.anyLong()))
                .thenReturn(Optional.of(new User()));

        Mockito.when(authenticationService.isCurrentAuthenticatedUser(ArgumentMatchers.anyLong()))
                .thenReturn(false);

        Mockito.when(messageSource.getMessage(ArgumentMatchers.anyString(), ArgumentMatchers.any(Object[].class), ArgumentMatchers.any(Locale.class)))
                .thenReturn("");

        // Assert
        Assertions.assertThatThrownBy(() -> userService.reverify(1L))
                .isInstanceOf(InvalidUserException.class);
    }

    @Test
    void reverify_withValidUserAndAuthentication_publishesOnVerificationNeededEvent() {
        // Arrange
        UserDto userDto = new UserDto();
        userDto.setEmailAddress("email@address.com");
        userDto.setUsername("username");

        Mockito.when(userRepository.findById(ArgumentMatchers.anyLong()))
                .thenReturn(Optional.of(new User()));

        Mockito.when(userRepository.save(ArgumentMatchers.any()))
                .thenReturn(new User());

        Mockito.when(authenticationService.isCurrentAuthenticatedUser(ArgumentMatchers.anyLong()))
                .thenReturn(true);

        Mockito.when(streamBridge.send(ArgumentMatchers.anyString(), ArgumentMatchers.any()))
                .thenReturn(true);

        Mockito.when(userMapper.fromUser(ArgumentMatchers.any()))
                .thenReturn(userDto);

        // Act
        userService.reverify(1L);

        // Assert
        Mockito.verify(streamBridge, Mockito.atMostOnce())
                .send(ArgumentMatchers.eq("trak-email-verification"), ArgumentMatchers.any(VerificationEvent.class));
    }

    @Test
    void requestRecovery_withNonExistentUser_doesntPublishOnRecoveryNeededEvent() {
        // Arrange
        Mockito.when(userRepository.findByEmailAddress(ArgumentMatchers.anyString()))
                .thenReturn(Optional.empty());

        // Act
        userService.requestRecovery("email");

        // Assert
        Mockito.verify(streamBridge, Mockito.never())
                .send(ArgumentMatchers.eq("trak-email-recovery"), ArgumentMatchers.any(RecoveryEvent.class));
    }

    @Test
    void requestRecovery_withUser_publishesOnRecoveryNeededEvent() {
        // Arrange
        Mockito.when(userRepository.findByEmailAddress(ArgumentMatchers.anyString()))
                .thenReturn(Optional.of(new User()));

        Mockito.when(userRepository.save(ArgumentMatchers.any()))
                .thenReturn(new User());

        // Act
        userService.requestRecovery("email");

        // Assert
        Mockito.verify(streamBridge, Mockito.atMostOnce())
                .send(ArgumentMatchers.eq("trak-email-recovery"), ArgumentMatchers.any(RecoveryEvent.class));
    }

    @Test
    void changePassword_withNoMatchingUser_throwsEntityNotFoundException() {
        // Arrange
        ChangePasswordRequestDto changePasswordRequestDto = new ChangePasswordRequestDto();

        Mockito.when(userRepository.findById(ArgumentMatchers.anyLong()))
                .thenReturn(Optional.empty());

        // Assert
        Assertions.assertThatThrownBy(() -> userService.changePassword(1L, changePasswordRequestDto))
                        .isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    void changePassword_withDifferentUser_throwsInvalidUserException() {
        // Arrange
        ChangePasswordRequestDto changePasswordRequestDto = new ChangePasswordRequestDto();

        Mockito.when(userRepository.findById(ArgumentMatchers.anyLong()))
                .thenReturn(Optional.of(new User()));

        Mockito.when(authenticationService.isCurrentAuthenticatedUser(ArgumentMatchers.anyLong()))
                .thenReturn(false);

        Mockito.when(messageSource.getMessage(ArgumentMatchers.anyString(), ArgumentMatchers.any(Object[].class), ArgumentMatchers.any(Locale.class)))
                .thenReturn("");

        // Assert
        Assertions.assertThatThrownBy(() -> userService.changePassword(1L, changePasswordRequestDto))
                .isInstanceOf(InvalidUserException.class);
    }

    @Test
    void changePassword_withNonMatchingCurrentPassword_returnsFalseCheckedResponse() {
        // Arrange
        ChangePasswordRequestDto changePasswordRequestDto = new ChangePasswordRequestDto();
        changePasswordRequestDto.setCurrentPassword("Password321");

        Mockito.when(userRepository.findById(ArgumentMatchers.anyLong()))
                .thenReturn(Optional.of(new User()));

        Mockito.when(authenticationService.isCurrentAuthenticatedUser(ArgumentMatchers.anyLong()))
                .thenReturn(true);

        Mockito.when(passwordEncoder.matches(ArgumentMatchers.anyString(), ArgumentMatchers.anyString()))
                .thenReturn(false);

        Mockito.when(messageSource.getMessage(ArgumentMatchers.anyString(), ArgumentMatchers.any(Object[].class), ArgumentMatchers.any(Locale.class)))
                .thenReturn("error");

        UserDto userDto = new UserDto();
        userDto.setPassword("Password123");

        Mockito.when(userMapper.fromUser(ArgumentMatchers.any()))
                .thenReturn(userDto);

        // Act
        CheckedResponse<Boolean> result = userService.changePassword(1L, changePasswordRequestDto);

        // Assert
        Assertions.assertThat(result.getData())
                .isFalse();
        Assertions.assertThat(result.isError())
                .isTrue();
        Assertions.assertThat(result.getErrorMessage())
                .isEqualTo("error");

        Mockito.verify(userRepository, Mockito.never())
                .save(ArgumentMatchers.any());
    }

    @Test
    void changePassword_withMatchingCurrentPassword_returnsTrueCheckedResponse() {
        // Arrange
        ChangePasswordRequestDto changePasswordRequestDto = new ChangePasswordRequestDto();
        changePasswordRequestDto.setCurrentPassword("Password123");

        UserDto userDto = new UserDto();
        userDto.setUsername("username");
        userDto.setPassword("Password123");
        userDto.setEmailAddress("test@traklibrary.com");

        Mockito.when(userRepository.findById(ArgumentMatchers.anyLong()))
                .thenReturn(Optional.of(new User()));

        Mockito.when(authenticationService.isCurrentAuthenticatedUser(ArgumentMatchers.anyLong()))
                .thenReturn(true);

        Mockito.when(passwordEncoder.matches(ArgumentMatchers.anyString(), ArgumentMatchers.anyString()))
                .thenReturn(true);

        Mockito.when(userRepository.save(ArgumentMatchers.any()))
                .thenReturn(new User());

        Mockito.when(streamBridge.send(ArgumentMatchers.anyString(), ArgumentMatchers.any()))
                .thenReturn(true);

        Mockito.when(userMapper.fromUser(ArgumentMatchers.any()))
                .thenReturn(userDto);

        // Act
        CheckedResponse<Boolean> result = userService.changePassword(1L, changePasswordRequestDto);

        // Assert
        Assertions.assertThat(result.getData())
                .isTrue();
        Assertions.assertThat(result.isError())
                .isFalse();
        Assertions.assertThat(result.getErrorMessage())
                .isEmpty();

        Mockito.verify(passwordEncoder, Mockito.atMostOnce())
                .encode(ArgumentMatchers.anyString());

        Mockito.verify(userRepository, Mockito.atMostOnce())
                .save(ArgumentMatchers.any());

        Mockito.verify(streamBridge, Mockito.atMostOnce())
                .send(ArgumentMatchers.eq("trak-email-password-changed"), ArgumentMatchers.any());
    }

    @Test
    void changeEmailAddress_withNoMatchingUser_throwsEntityNotFoundException() {
        // Arrange
        Mockito.when(userRepository.findById(ArgumentMatchers.anyLong()))
                .thenReturn(Optional.empty());

        // Assert
        Assertions.assertThatThrownBy(() -> userService.changeEmailAddress(1L, "test@traklibrary.com"))
                .isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    void changeEmailAddress_withDifferentUser_throwsInvalidUserException() {
        // Arrange
        Mockito.when(userRepository.findById(ArgumentMatchers.anyLong()))
                .thenReturn(Optional.of(new User()));

        Mockito.when(authenticationService.isCurrentAuthenticatedUser(ArgumentMatchers.anyLong()))
                .thenReturn(false);

        Mockito.when(messageSource.getMessage(ArgumentMatchers.anyString(), ArgumentMatchers.any(Object[].class), ArgumentMatchers.any(Locale.class)))
                .thenReturn("");

        // Assert
        Assertions.assertThatThrownBy(() -> userService.changeEmailAddress(1L, "test@traklibrary.com"))
                .isInstanceOf(InvalidUserException.class);
    }

    @Test
    void changeEmailAddress_withMatchingEmailAddress_returnsFalseCheckedResponse() {
        // Arrange
        UserDto userDto = new UserDto();
        userDto.setEmailAddress("test@traklibrary.com");

        Mockito.when(userRepository.findById(ArgumentMatchers.anyLong()))
                .thenReturn(Optional.of(new User()));

        Mockito.when(authenticationService.isCurrentAuthenticatedUser(ArgumentMatchers.anyLong()))
                .thenReturn(true);

        Mockito.when(messageSource.getMessage(ArgumentMatchers.anyString(), ArgumentMatchers.any(Object[].class), ArgumentMatchers.any(Locale.class)))
                .thenReturn("error");

        Mockito.when(userMapper.fromUser(ArgumentMatchers.any()))
                .thenReturn(userDto);

        // Act
        CheckedResponse<Boolean> result = userService.changeEmailAddress(1L, userDto.getEmailAddress());

        // Assert
        Assertions.assertThat(result.getData())
                .isFalse();
        Assertions.assertThat(result.isError())
                .isTrue();
        Assertions.assertThat(result.getErrorMessage())
                .isEqualTo("error");

        Mockito.verify(userRepository, Mockito.never())
                .save(ArgumentMatchers.any());
    }

    @Test
    void changeEmailAddress_withNonMatchingEmailAddressAndValidUser_returnsTrueCheckedResponse() {
        // Arrange
        Mockito.when(userRepository.findById(ArgumentMatchers.anyLong()))
                .thenReturn(Optional.of(new User()));

        Mockito.when(authenticationService.isCurrentAuthenticatedUser(ArgumentMatchers.anyLong()))
                .thenReturn(true);

        Mockito.when(userRepository.save(ArgumentMatchers.any()))
                .thenReturn(new User());

        Mockito.when(streamBridge.send(ArgumentMatchers.anyString(), ArgumentMatchers.any()))
                .thenReturn(true);

        UserDto userDto = Mockito.mock(UserDto.class);
        Mockito.when(userDto.getEmailAddress())
                        .thenReturn("email.address");

        Mockito.when(userMapper.fromUser(ArgumentMatchers.any()))
                .thenReturn(userDto);

        // Act
        CheckedResponse<Boolean> result = userService.changeEmailAddress(1L, "test@traklibrary.com");

        // Assert
        Assertions.assertThat(result.getData())
                .isTrue();
        Assertions.assertThat(result.isError())
                .isFalse();
        Assertions.assertThat(result.getErrorMessage())
                .isEmpty();

        Mockito.verify(streamBridge, Mockito.atMostOnce())
                .send(ArgumentMatchers.eq("trak-email-verification"), ArgumentMatchers.any(VerificationEvent.class));

        Mockito.verify(userRepository, Mockito.atMostOnce())
                .save(ArgumentMatchers.any());

        Mockito.verify(userDto, Mockito.atMost(1))
                .setEmailAddress(ArgumentMatchers.anyString());
    }
}

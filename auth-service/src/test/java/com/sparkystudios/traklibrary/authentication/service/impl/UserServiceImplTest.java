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
import org.junit.jupiter.api.Assertions;
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
        Assertions.assertThrows(UsernameNotFoundException.class, () -> userService.loadUserByUsername(""));
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
                .userToUserDto(ArgumentMatchers.any());
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
        Assertions.assertNull(result.getData(), "There should be no response data if the username is already in use.");
        Assertions.assertEquals("username-error", result.getErrorMessage(), "The error message doesn't match.");
        Assertions.assertTrue(result.isError(), "The response should be an error for a user that already exists.");

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
        Assertions.assertNull(result.getData(), "There should be no response data if the email address is already in use.");
        Assertions.assertEquals("email-error", result.getErrorMessage(), "The error message doesn't match.");
        Assertions.assertTrue(result.isError(), "The response should be an error for a email address that already exists.");

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

        Mockito.when(userRoleRepository.findByRole(ArgumentMatchers.anyString()))
                .thenReturn(Optional.empty());

        Mockito.when(messageSource.getMessage(ArgumentMatchers.anyString(), ArgumentMatchers.any(Object[].class), ArgumentMatchers.any(Locale.class)))
                .thenReturn("");

        RegistrationRequestDto registrationRequestDto = new RegistrationRequestDto();
        registrationRequestDto.setUsername("username");
        registrationRequestDto.setEmailAddress("email.address@email.com");
        registrationRequestDto.setPassword("password");

        // Assert
        Assertions.assertThrows(EntityNotFoundException.class, () -> userService.save(registrationRequestDto));
    }

    @Test
    void save_withValidCredentialsAndUserRole_savesUserAndMakesUserRoleXrefAndPublishesEvent() {
        // Arrange
        Mockito.when(userRepository.findByUsername(ArgumentMatchers.anyString()))
                .thenReturn(Optional.empty());

        Mockito.when(userRepository.findByEmailAddress(ArgumentMatchers.anyString()))
                .thenReturn(Optional.empty());

        Mockito.when(userRoleRepository.findByRole(ArgumentMatchers.anyString()))
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
        Assertions.assertNotNull(result, "The response of the save should not be null.");
        Assertions.assertEquals("", result.getErrorMessage(), "There should be no error message for a valid response.");
        Assertions.assertFalse(result.isError(), "The response should have no errors for a valid response.");

        Mockito.verify(userRepository, Mockito.atMostOnce())
                .save(ArgumentMatchers.any());

        Mockito.verify(streamBridge, Mockito.atMostOnce())
                .send(ArgumentMatchers.eq("trak-email-verification"), ArgumentMatchers.any(VerificationEvent.class));
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
        Assertions.assertNull(result.getData(), "There should be no user data if the username wasn't found.");
        Assertions.assertEquals("user-error", result.getErrorMessage(), "The error message doesn't match.");
        Assertions.assertTrue(result.isError(), "The response should be an error for a username that doesn't exist.");

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
        Assertions.assertNull(result.getData(), "There should be no user data if the user has no recovery token.");
        Assertions.assertEquals("user-error", result.getErrorMessage(), "The error message doesn't match.");
        Assertions.assertTrue(result.isError(), "The response should be an error if the user has no recovery token.");

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
        Assertions.assertNull(result.getData(), "There should be no user data if the recovery token doesn't match.");
        Assertions.assertEquals("user-error", result.getErrorMessage(), "The error message doesn't match.");
        Assertions.assertTrue(result.isError(), "The response should be an error if the recovery token doesn't match.");

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

        RecoveryRequestDto recoveryRequestDto = new RecoveryRequestDto();
        recoveryRequestDto.setUsername("username");
        recoveryRequestDto.setRecoveryToken("recovery");
        recoveryRequestDto.setPassword("password");

        // Act
        CheckedResponse<UserResponseDto> result = userService.update(recoveryRequestDto);

        // Assert
        Assertions.assertNotNull(result, "The response of the update should not be null.");
        Assertions.assertEquals("", result.getErrorMessage(), "There should be no error message for a valid response.");
        Assertions.assertFalse(result.isError(), "The response should have no errors for a valid response.");

        Mockito.verify(userRepository, Mockito.atMostOnce())
                .save(ArgumentMatchers.any());
    }

    @Test
    void deleteByIde_withNoMatchingUser_throwsEntityNotFoundException() {
        // Arrange
        Mockito.when(userRepository.findById(ArgumentMatchers.anyLong()))
                .thenReturn(Optional.empty());

        // Assert
        Assertions.assertThrows(EntityNotFoundException.class, () -> userService.deleteById(1L));
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
        Assertions.assertThrows(InvalidUserException.class, () -> userService.deleteById(1L));
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
        Assertions.assertThrows(EntityNotFoundException.class, () -> userService.verify(1L, "11111"));
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
        Assertions.assertThrows(InvalidUserException.class, () -> userService.verify(1L, "11111"));
    }

    @Test
    void verify_withVerifiedUser_doesntUpdateVerificationStatus() {
        // Arrange
        User user = new User();
        user.setVerified(true);

        Mockito.when(userRepository.findById(ArgumentMatchers.anyLong()))
                .thenReturn(Optional.of(user));

        Mockito.when(authenticationService.isCurrentAuthenticatedUser(ArgumentMatchers.anyLong()))
                .thenReturn(true);

        // Act
        CheckedResponse<Boolean> result = userService.verify(1L, "11111");

        // Assert
        Assertions.assertTrue(result.getData(), "The response should be true if the user is already verified.");
        Assertions.assertFalse(result.isError(), "There should be no errors for an already verified user.");
        Assertions.assertEquals("", result.getErrorMessage(), "The error message should be empty for an already verified user.");

        Mockito.verify(userRepository, Mockito.never())
                .save(ArgumentMatchers.any());
    }

    @Test
    void verify_withNonVerifiedUserButIncorrectVerificationCode_returnsCheckedResponseWithError() {
        // Arrange
        User user = new User();
        user.setVerificationCode("11112");

        Mockito.when(userRepository.findById(ArgumentMatchers.anyLong()))
                .thenReturn(Optional.of(user));

        Mockito.when(authenticationService.isCurrentAuthenticatedUser(ArgumentMatchers.anyLong()))
                .thenReturn(true);

        Mockito.when(messageSource.getMessage(ArgumentMatchers.anyString(), ArgumentMatchers.any(Object[].class), ArgumentMatchers.any(Locale.class)))
                .thenReturn("error");

        // Act
        CheckedResponse<Boolean> result = userService.verify(1L, "11111");

        // Assert
        Assertions.assertFalse(result.getData(), "The response should be false if the verification code is incorrect.");
        Assertions.assertTrue(result.isError(), "There should be errors if the verification code is incorrect.");
        Assertions.assertEquals("error", result.getErrorMessage(), "The error message should contain an error message.");

        Mockito.verify(userRepository, Mockito.never())
                .save(ArgumentMatchers.any());
    }

    @Test
    void verify_withNonVerifiedUserWithNullVerificationCode_returnsCheckedResponseWithError() {
        // Arrange
        User user = new User();

        Mockito.when(userRepository.findById(ArgumentMatchers.anyLong()))
                .thenReturn(Optional.of(user));

        Mockito.when(authenticationService.isCurrentAuthenticatedUser(ArgumentMatchers.anyLong()))
                .thenReturn(true);

        Mockito.when(messageSource.getMessage(ArgumentMatchers.anyString(), ArgumentMatchers.any(Object[].class), ArgumentMatchers.any(Locale.class)))
                .thenReturn("error");

        // Act
        CheckedResponse<Boolean> result = userService.verify(1L, "11111");

        // Assert
        Assertions.assertFalse(result.getData(), "The response should be false if the user verification code is null.");
        Assertions.assertTrue(result.isError(), "There should be errors if the user verification code is null.");
        Assertions.assertEquals("error", result.getErrorMessage(), "The error message should contain an error message.");

        Mockito.verify(userRepository, Mockito.never())
                .save(ArgumentMatchers.any());
    }

    @Test
    void verify_withNonVerifiedUserWithCorrectVerificationCode_updatesUser() {
        // Arrange
        User user = new User();
        user.setVerificationCode("11111");

        Mockito.when(userRepository.findById(ArgumentMatchers.anyLong()))
                .thenReturn(Optional.of(user));

        Mockito.when(authenticationService.isCurrentAuthenticatedUser(ArgumentMatchers.anyLong()))
                .thenReturn(true);

        Mockito.when(userRepository.save(ArgumentMatchers.any()))
                .thenReturn(new User());

        // Act
        CheckedResponse<Boolean> result = userService.verify(1L, "11111");

        // Assert
        Assertions.assertTrue(result.getData(), "The response should be true if the user has been successfully verified.");
        Assertions.assertFalse(result.isError(), "There should be no errors for an successfully verified user.");
        Assertions.assertEquals("", result.getErrorMessage(), "The error message should be empty for a successfully verified user.");

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
        Assertions.assertThrows(EntityNotFoundException.class, () -> userService.reverify(1L));
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
        Assertions.assertThrows(InvalidUserException.class, () -> userService.reverify(1L));
    }

    @Test
    void reverify_withValidUserAndAuthentication_publishesOnVerificationNeededEvent() {
        // Arrange
        User user = new User();
        user.setEmailAddress("email@address.com");
        user.setUsername("username");

        Mockito.when(userRepository.findById(ArgumentMatchers.anyLong()))
                .thenReturn(Optional.of(user));

        Mockito.when(userRepository.save(ArgumentMatchers.any()))
                .thenReturn(new User());

        Mockito.when(authenticationService.isCurrentAuthenticatedUser(ArgumentMatchers.anyLong()))
                .thenReturn(true);

        Mockito.when(streamBridge.send(ArgumentMatchers.anyString(), ArgumentMatchers.any()))
                .thenReturn(true);

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
        Assertions.assertThrows(EntityNotFoundException.class, () -> userService.changePassword(1L, changePasswordRequestDto));
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
        Assertions.assertThrows(InvalidUserException.class, () -> userService.changePassword(1L, changePasswordRequestDto));
    }

    @Test
    void changePassword_withNonMatchingCurrentPassword_returnsFalseCheckedResponse() {
        // Arrange
        ChangePasswordRequestDto changePasswordRequestDto = new ChangePasswordRequestDto();
        changePasswordRequestDto.setCurrentPassword("Password321");

        User user = new User();
        user.setPassword("Password123");

        Mockito.when(userRepository.findById(ArgumentMatchers.anyLong()))
                .thenReturn(Optional.of(user));

        Mockito.when(authenticationService.isCurrentAuthenticatedUser(ArgumentMatchers.anyLong()))
                .thenReturn(true);

        Mockito.when(passwordEncoder.matches(ArgumentMatchers.anyString(), ArgumentMatchers.anyString()))
                .thenReturn(false);

        Mockito.when(messageSource.getMessage(ArgumentMatchers.anyString(), ArgumentMatchers.any(Object[].class), ArgumentMatchers.any(Locale.class)))
                .thenReturn("error");

        // Act
        CheckedResponse<Boolean> result = userService.changePassword(1L, changePasswordRequestDto);

        // Assert
        Assertions.assertFalse(result.getData(), "The response should be false if the user recovery token is null.");
        Assertions.assertTrue(result.isError(), "There should be errors if the user recovery token is null.");
        Assertions.assertEquals("error", result.getErrorMessage(), "The error message should contain an error message.");

        Mockito.verify(userRepository, Mockito.never())
                .save(ArgumentMatchers.any());
    }

    @Test
    void changePassword_withMatchingCurrentPassword_returnsTrueCheckedResponse() {
        // Arrange
        ChangePasswordRequestDto changePasswordRequestDto = new ChangePasswordRequestDto();
        changePasswordRequestDto.setCurrentPassword("Password123");

        User user = new User();
        user.setUsername("username");
        user.setPassword("Password123");
        user.setEmailAddress("test@traklibrary.com");

        Mockito.when(userRepository.findById(ArgumentMatchers.anyLong()))
                .thenReturn(Optional.of(user));

        Mockito.when(authenticationService.isCurrentAuthenticatedUser(ArgumentMatchers.anyLong()))
                .thenReturn(true);

        Mockito.when(passwordEncoder.matches(ArgumentMatchers.anyString(), ArgumentMatchers.anyString()))
                .thenReturn(true);

        Mockito.when(userRepository.save(ArgumentMatchers.any()))
                .thenReturn(user);

        Mockito.when(streamBridge.send(ArgumentMatchers.anyString(), ArgumentMatchers.any()))
                .thenReturn(true);

        // Act
        CheckedResponse<Boolean> result = userService.changePassword(1L, changePasswordRequestDto);

        // Assert
        Assertions.assertTrue(result.getData(), "The response should be true if the user's password was successfully changed.");
        Assertions.assertFalse(result.isError(), "There should be no errors for a successful password change.");
        Assertions.assertEquals("", result.getErrorMessage(), "The error message should be empty for a successful password change.");

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
        Assertions.assertThrows(EntityNotFoundException.class, () -> userService.changeEmailAddress(1L, "test@traklibrary.com"));
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
        Assertions.assertThrows(InvalidUserException.class, () -> userService.changeEmailAddress(1L, "test@traklibrary.com"));
    }

    @Test
    void changeEmailAddress_withMatchingEmailAddress_returnsFalseCheckedResponse() {
        // Arrange
        User user = new User();
        user.setEmailAddress("test@traklibrary.com");

        Mockito.when(userRepository.findById(ArgumentMatchers.anyLong()))
                .thenReturn(Optional.of(user));

        Mockito.when(authenticationService.isCurrentAuthenticatedUser(ArgumentMatchers.anyLong()))
                .thenReturn(true);

        Mockito.when(messageSource.getMessage(ArgumentMatchers.anyString(), ArgumentMatchers.any(Object[].class), ArgumentMatchers.any(Locale.class)))
                .thenReturn("error");

        // Act
        CheckedResponse<Boolean> result = userService.changeEmailAddress(1L, user.getEmailAddress());

        // Assert
        Assertions.assertFalse(result.getData(), "The response should be false if email addresses match.");
        Assertions.assertTrue(result.isError(), "There should be errors if the email addresses match.");
        Assertions.assertEquals("error", result.getErrorMessage(), "The error message should contain an error message.");

        Mockito.verify(userRepository, Mockito.never())
                .save(ArgumentMatchers.any());
    }

    @Test
    void changeEmailAddress_withNonMatchingEmailAddressAndValidUser_returnsTrueCheckedResponse() {
        // Arrange
        User user = Mockito.spy(User.class);
        user.setEmailAddress("email.address");

        Mockito.when(userRepository.findById(ArgumentMatchers.anyLong()))
                .thenReturn(Optional.of(user));

        Mockito.when(authenticationService.isCurrentAuthenticatedUser(ArgumentMatchers.anyLong()))
                .thenReturn(true);

        Mockito.when(userRepository.save(ArgumentMatchers.any()))
                .thenReturn(user);

        Mockito.when(streamBridge.send(ArgumentMatchers.anyString(), ArgumentMatchers.any()))
                .thenReturn(true);

        // Act
        CheckedResponse<Boolean> result = userService.changeEmailAddress(1L, "test@traklibrary.com");

        // Assert
        Assertions.assertTrue(result.getData(), "The response should be true if the user's email address was successfully changed.");
        Assertions.assertFalse(result.isError(), "There should be no errors for a successful email address change.");
        Assertions.assertEquals("", result.getErrorMessage(), "The error message should be empty for a successful email address change.");

        Mockito.verify(streamBridge, Mockito.atMostOnce())
                .send(ArgumentMatchers.eq("trak-email-verification"), ArgumentMatchers.any(VerificationEvent.class));

        Mockito.verify(userRepository, Mockito.atMostOnce())
                .save(ArgumentMatchers.any());

        Mockito.verify(user, Mockito.atMost(2))
                .setEmailAddress(ArgumentMatchers.anyString());
    }
}

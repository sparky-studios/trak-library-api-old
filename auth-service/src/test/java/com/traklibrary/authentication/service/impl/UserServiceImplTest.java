package com.traklibrary.authentication.service.impl;

import com.traklibrary.authentication.domain.User;
import com.traklibrary.authentication.domain.UserRole;
import com.traklibrary.authentication.domain.UserRoleXref;
import com.traklibrary.authentication.repository.UserRepository;
import com.traklibrary.authentication.repository.UserRoleRepository;
import com.traklibrary.authentication.repository.UserRoleXrefRepository;
import com.traklibrary.authentication.service.AuthenticationService;
import com.traklibrary.authentication.service.dto.CheckedResponse;
import com.traklibrary.authentication.service.dto.RecoveryRequestDto;
import com.traklibrary.authentication.service.dto.RegistrationRequestDto;
import com.traklibrary.authentication.service.dto.UserResponseDto;
import com.traklibrary.authentication.service.exception.InvalidUserException;
import com.traklibrary.authentication.service.mapper.UserResponseMapper;
import com.traklibrary.authentication.service.mapper.UserMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.MessageSource;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.persistence.EntityNotFoundException;
import java.util.Locale;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserRoleRepository userRoleRepository;

    @Mock
    private UserRoleXrefRepository userRoleXrefRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Spy
    private UserMapper userMapper;

    @Spy
    private UserResponseMapper userResponseMapper;

    @Mock
    private MessageSource messageSource;

    @Mock
    private ApplicationEventPublisher applicationEventPublisher;

    @Mock
    private AuthenticationService authenticationService;

    @InjectMocks
    private UserServiceImpl userService;

    @Test
    public void loadUserByUsername_withNonExistentUser_throwsUsernameNotFoundException() {
        // Arrange
        Mockito.when(userRepository.findByUsername(ArgumentMatchers.anyString()))
                .thenReturn(Optional.empty());

        // Assert
        Assertions.assertThrows(UsernameNotFoundException.class, () -> userService.loadUserByUsername(""));
    }

    @Test
    public void loadUserByUsername_withExistingUser_returnsMappedUserDto() {
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
    public void save_withExistingUsername_throwsEntityExistsException() {
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
        CheckedResponse<UserResponseDto> result = userService.save(registrationRequestDto);

        // Assert
        Assertions.assertNull(result.getData(), "There should be no user data if the username is already in use.");
        Assertions.assertEquals("username-error", result.getErrorMessage(), "The error message doesn't match.");
        Assertions.assertTrue(result.isError(), "The response should be an error for a user that already exists.");

        Mockito.verify(userRepository, Mockito.never())
                .save(ArgumentMatchers.any());
    }

    @Test
    public void save_withExistingEmailAddress_throwsEntityExistsException() {
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
        CheckedResponse<UserResponseDto> result = userService.save(registrationRequestDto);

        // Assert
        Assertions.assertNull(result.getData(), "There should be no user data if the email address is already in use.");
        Assertions.assertEquals("email-error", result.getErrorMessage(), "The error message doesn't match.");
        Assertions.assertTrue(result.isError(), "The response should be an error for a email address that already exists.");

        Mockito.verify(userRepository, Mockito.never())
                .save(ArgumentMatchers.any());
    }

    @Test
    public void save_withMissingUserRole_throwsEntityNotFoundException() {
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
    public void save_withValidCredentialsAndUserRole_savesUserAndMakesUserRoleXrefAndPublishesEvent() {
        // Arrange
        Mockito.when(userRepository.findByUsername(ArgumentMatchers.anyString()))
                .thenReturn(Optional.empty());

        Mockito.when(userRepository.findByEmailAddress(ArgumentMatchers.anyString()))
                .thenReturn(Optional.empty());

        Mockito.when(userRoleRepository.findByRole(ArgumentMatchers.anyString()))
                .thenReturn(Optional.of(new UserRole()));

        Mockito.doNothing()
                .when(applicationEventPublisher).publishEvent(ArgumentMatchers.any());

        User user = new User();
        user.setEmailAddress("random-address@trak.com");

        Mockito.when(userRepository.save(ArgumentMatchers.any()))
                .thenReturn(user);

        Mockito.when(userRoleXrefRepository.save(ArgumentMatchers.any()))
                .thenReturn(new UserRoleXref());

        Mockito.when(passwordEncoder.encode(ArgumentMatchers.anyString()))
                .thenReturn("password");

        RegistrationRequestDto registrationRequestDto = new RegistrationRequestDto();
        registrationRequestDto.setUsername("username");
        registrationRequestDto.setEmailAddress("email.address@email.com");
        registrationRequestDto.setPassword("password");

        // Act
        CheckedResponse<UserResponseDto> result = userService.save(registrationRequestDto);

        // Assert
        Assertions.assertNotNull(result, "The response of the save should not be null.");
        Assertions.assertEquals("", result.getErrorMessage(), "There should be no error message for a valid response.");
        Assertions.assertFalse(result.isError(), "The response should have no errors for a valid response.");

        Mockito.verify(userRepository, Mockito.atMostOnce())
                .save(ArgumentMatchers.any());

        Mockito.verify(userRoleXrefRepository, Mockito.atMostOnce())
                .save(ArgumentMatchers.any());

        Mockito.verify(applicationEventPublisher)
                .publishEvent(ArgumentMatchers.any());
    }

    @Test
    public void update_withNonExistentUser_returnsCheckedResponseWithError() {
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
    public void update_withNullUserRecoveryToken_returnsCheckedResponseWithError() {
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
    public void update_withIncorrectRecoveryToken_returnsCheckedResponseWithError() {
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
    public void update_withValidRecoveryToken_savesUserAndReturnsValidCheckedResponse() {
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
    public void findByUsername_withNoMatchingUser_throwsEntityNotFoundException() {
        // Arrange
        Mockito.when(userRepository.findByUsername(ArgumentMatchers.anyString()))
                .thenReturn(Optional.empty());

        // Assert
        Assertions.assertThrows(EntityNotFoundException.class, () -> userService.findByUsername("username"));
    }

    @Test
    public void findByUsername_withDifferentUser_throwsInvalidUserException() {
        // Arrange
        Mockito.when(userRepository.findByUsername(ArgumentMatchers.anyString()))
                .thenReturn(Optional.of(new User()));

        Mockito.when(authenticationService.isCurrentAuthenticatedUser(ArgumentMatchers.anyLong()))
                .thenReturn(false);

        Mockito.when(messageSource.getMessage(ArgumentMatchers.anyString(), ArgumentMatchers.any(Object[].class), ArgumentMatchers.any(Locale.class)))
                .thenReturn("");

        // Assert
        Assertions.assertThrows(InvalidUserException.class, () -> userService.findByUsername("username"));
    }

    @Test
    public void findByUsername_withMatchingUser_returnsUserResponse() {
        // Arrange
        User user = new User();
        user.setId(5L);
        user.setUsername("test-username");
        user.setVerified(true);

        Mockito.when(userRepository.findByUsername(ArgumentMatchers.anyString()))
                .thenReturn(Optional.of(user));

        Mockito.when(authenticationService.isCurrentAuthenticatedUser(ArgumentMatchers.anyLong()))
                .thenReturn(true);

        // Act
        userService.findByUsername("username");

        // Assert
        Mockito.verify(userResponseMapper, Mockito.atMostOnce())
                .userToUserResponseDto(ArgumentMatchers.any());
    }

    @Test
    public void createVerificationCode_withNullUser_returnsEmptyString() {
        // Arrange
        Mockito.when(userRepository.findByUsername(ArgumentMatchers.anyString()))
                .thenReturn(Optional.empty());

        // Act
        String result = userService.createVerificationCode("username");

        // Assert
        Assertions.assertEquals("", result, "The string should be empty if no user is found.");
    }

    @Test
    public void createVerificationCode_withExistingUser_savesAndReturnsToken() {
        // Arrange
        Mockito.when(userRepository.findByUsername(ArgumentMatchers.anyString()))
                .thenReturn(Optional.of(new User()));

        Mockito.when(userRepository.save(ArgumentMatchers.any()))
                .thenReturn(new User());

        // Act
        String result = userService.createVerificationCode("username");

        // Assert
        Assertions.assertEquals(5, result.length(), "The token should contain 5 characters.");
        Mockito.verify(userRepository, Mockito.atMostOnce())
                .save(ArgumentMatchers.any());
    }

    @Test
    public void createRecoveryToken_withNullUser_returnsEmptyString() {
        // Arrange
        Mockito.when(userRepository.findByUsername(ArgumentMatchers.anyString()))
                .thenReturn(Optional.empty());

        // Act
        String result = userService.createRecoveryToken("username");

        // Assert
        Assertions.assertEquals("", result, "The recovery token should be empty if no user is found.");
    }

    @Test
    public void createRecoveryToken_withExistingUser_savesAndReturnsToken() {
        // Arrange
        Mockito.when(userRepository.findByUsername(ArgumentMatchers.anyString()))
                .thenReturn(Optional.of(new User()));

        Mockito.when(userRepository.save(ArgumentMatchers.any()))
                .thenReturn(new User());

        // Act
        String result = userService.createRecoveryToken("username");

        // Assert
        Assertions.assertEquals(30, result.length(), "The recovery token should contain 30 characters.");
        Mockito.verify(userRepository, Mockito.atMostOnce())
                .save(ArgumentMatchers.any());
    }

    @Test
    public void verify_withNoMatchingUser_throwsEntityNotFoundException() {
        // Arrange
        Mockito.when(userRepository.findByUsername(ArgumentMatchers.anyString()))
                .thenReturn(Optional.empty());

        // Assert
        Assertions.assertThrows(EntityNotFoundException.class, () -> userService.verify("username", "11111"));
    }

    @Test
    public void verify_withDifferentUser_throwsInvalidUserException() {
        // Arrange
        Mockito.when(userRepository.findByUsername(ArgumentMatchers.anyString()))
                .thenReturn(Optional.of(new User()));

        Mockito.when(authenticationService.isCurrentAuthenticatedUser(ArgumentMatchers.anyLong()))
                .thenReturn(false);

        Mockito.when(messageSource.getMessage(ArgumentMatchers.anyString(), ArgumentMatchers.any(Object[].class), ArgumentMatchers.any(Locale.class)))
                .thenReturn("");

        // Assert
        Assertions.assertThrows(InvalidUserException.class, () -> userService.verify("username", "11111"));
    }

    @Test
    public void verify_withVerifiedUser_doesntUpdateVerificationStatus() {
        // Arrange
        User user = new User();
        user.setVerified(true);

        Mockito.when(userRepository.findByUsername(ArgumentMatchers.anyString()))
                .thenReturn(Optional.of(user));

        Mockito.when(authenticationService.isCurrentAuthenticatedUser(ArgumentMatchers.anyLong()))
                .thenReturn(true);

        // Act
        CheckedResponse<Boolean> result = userService.verify("username", "11111");

        // Assert
        Assertions.assertTrue(result.getData(), "The response should be true if the user is already verified.");
        Assertions.assertFalse(result.isError(), "There should be no errors for an already verified user.");
        Assertions.assertEquals("", result.getErrorMessage(), "The error message should be empty for an already verified user.");

        Mockito.verify(userRepository, Mockito.never())
                .save(ArgumentMatchers.any());
    }

    @Test
    public void verify_withNonVerifiedUserButIncorrectVerificationCode_returnsCheckedResponseWithError() {
        // Arrange
        User user = new User();
        user.setVerificationCode("11112");

        Mockito.when(userRepository.findByUsername(ArgumentMatchers.anyString()))
                .thenReturn(Optional.of(user));

        Mockito.when(authenticationService.isCurrentAuthenticatedUser(ArgumentMatchers.anyLong()))
                .thenReturn(true);

        Mockito.when(messageSource.getMessage(ArgumentMatchers.anyString(), ArgumentMatchers.any(Object[].class), ArgumentMatchers.any(Locale.class)))
                .thenReturn("error");

        // Act
        CheckedResponse<Boolean> result = userService.verify("username", "11111");

        // Assert
        Assertions.assertFalse(result.getData(), "The response should be false if the verification code is incorrect.");
        Assertions.assertTrue(result.isError(), "There should be errors if the verification code is incorrect.");
        Assertions.assertEquals("error", result.getErrorMessage(), "The error message should contain an error message.");

        Mockito.verify(userRepository, Mockito.never())
                .save(ArgumentMatchers.any());
    }

    @Test
    public void verify_withNonVerifiedUserWithNullVerificationCode_returnsCheckedResponseWithError() {
        // Arrange
        User user = new User();

        Mockito.when(userRepository.findByUsername(ArgumentMatchers.anyString()))
                .thenReturn(Optional.of(user));

        Mockito.when(authenticationService.isCurrentAuthenticatedUser(ArgumentMatchers.anyLong()))
                .thenReturn(true);

        Mockito.when(messageSource.getMessage(ArgumentMatchers.anyString(), ArgumentMatchers.any(Object[].class), ArgumentMatchers.any(Locale.class)))
                .thenReturn("error");

        // Act
        CheckedResponse<Boolean> result = userService.verify("username", "11111");

        // Assert
        Assertions.assertFalse(result.getData(), "The response should be false if the user verification code is null.");
        Assertions.assertTrue(result.isError(), "There should be errors if the user verification code is null.");
        Assertions.assertEquals("error", result.getErrorMessage(), "The error message should contain an error message.");

        Mockito.verify(userRepository, Mockito.never())
                .save(ArgumentMatchers.any());
    }

    @Test
    public void verify_withNonVerifiedUserWithCorrectVerificationCode_updatesUser() {
        // Arrange
        User user = new User();
        user.setVerificationCode("11111");

        Mockito.when(userRepository.findByUsername(ArgumentMatchers.anyString()))
                .thenReturn(Optional.of(user));

        Mockito.when(authenticationService.isCurrentAuthenticatedUser(ArgumentMatchers.anyLong()))
                .thenReturn(true);

        Mockito.when(userRepository.save(ArgumentMatchers.any()))
                .thenReturn(new User());

        // Act
        CheckedResponse<Boolean> result = userService.verify("username", "11111");

        // Assert
        Assertions.assertTrue(result.getData(), "The response should be true if the user has been successfully verified.");
        Assertions.assertFalse(result.isError(), "There should be no errors for an successfully verified user.");
        Assertions.assertEquals("", result.getErrorMessage(), "The error message should be empty for a successfully verified user.");

        Mockito.verify(userRepository, Mockito.atMostOnce())
                .save(ArgumentMatchers.any());
    }

    @Test
    public void reverify_withNoMatchingUser_throwsEntityNotFoundException() {
        // Arrange
        Mockito.when(userRepository.findByUsername(ArgumentMatchers.anyString()))
                .thenReturn(Optional.empty());

        Mockito.when(messageSource.getMessage(ArgumentMatchers.anyString(), ArgumentMatchers.any(Object[].class), ArgumentMatchers.any(Locale.class)))
                .thenReturn("");

        // Assert
        Assertions.assertThrows(EntityNotFoundException.class, () -> userService.reverify("username"));
    }

    @Test
    public void reverify_withDifferentUser_throwsInvalidUserException() {
        // Arrange
        Mockito.when(userRepository.findByUsername(ArgumentMatchers.anyString()))
                .thenReturn(Optional.of(new User()));

        Mockito.when(authenticationService.isCurrentAuthenticatedUser(ArgumentMatchers.anyLong()))
                .thenReturn(false);

        Mockito.when(messageSource.getMessage(ArgumentMatchers.anyString(), ArgumentMatchers.any(Object[].class), ArgumentMatchers.any(Locale.class)))
                .thenReturn("");

        // Assert
        Assertions.assertThrows(InvalidUserException.class, () -> userService.reverify("username"));
    }

    @Test
    public void reverify_withValidUserAndAuthentication_publishesOnVerificationNeededEvent() {
        // Arrange
        User user = new User();
        user.setEmailAddress("email@address.com");
        user.setUsername("username");

        Mockito.when(userRepository.findByUsername(ArgumentMatchers.anyString()))
                .thenReturn(Optional.of(user));

        Mockito.when(authenticationService.isCurrentAuthenticatedUser(ArgumentMatchers.anyLong()))
                .thenReturn(true);

        Mockito.doNothing().when(applicationEventPublisher)
                .publishEvent(ArgumentMatchers.any());

        // Act
        userService.reverify("username");

        // Assert
        Mockito.verify(applicationEventPublisher, Mockito.atMostOnce())
                .publishEvent(ArgumentMatchers.any());
    }

    @Test
    public void requestRecovery_withNonExistentUser_doesntPublishOnRecoveryNeededEvent() {
        // Arrange
        Mockito.when(userRepository.findByEmailAddress(ArgumentMatchers.anyString()))
                .thenReturn(Optional.empty());

        // Act
        userService.requestRecovery("email");

        // Assert
        Mockito.verify(applicationEventPublisher, Mockito.never())
                .publishEvent(ArgumentMatchers.any());
    }

    @Test
    public void requestRecovery_withUser_publishesOnRecoveryNeededEvent() {
        // Arrange
        Mockito.when(userRepository.findByEmailAddress(ArgumentMatchers.anyString()))
                .thenReturn(Optional.of(new User()));

        // Act
        userService.requestRecovery("email");

        // Assert
        Mockito.verify(applicationEventPublisher, Mockito.atMostOnce())
                .publishEvent(ArgumentMatchers.any());
    }
}

package com.sparky.trak.authentication.service.impl;

import com.sparky.trak.authentication.domain.User;
import com.sparky.trak.authentication.domain.UserRole;
import com.sparky.trak.authentication.domain.UserRoleXref;
import com.sparky.trak.authentication.repository.UserRepository;
import com.sparky.trak.authentication.repository.UserRoleRepository;
import com.sparky.trak.authentication.repository.UserRoleXrefRepository;
import com.sparky.trak.authentication.service.AuthenticationService;
import com.sparky.trak.authentication.service.dto.CheckedResponse;
import com.sparky.trak.authentication.service.dto.RegistrationRequestDto;
import com.sparky.trak.authentication.service.dto.UserResponseDto;
import com.sparky.trak.authentication.service.exception.InvalidUserException;
import com.sparky.trak.authentication.service.exception.VerificationFailedException;
import com.sparky.trak.authentication.service.mapper.UserResponseMapper;
import com.sparky.trak.authentication.service.mapper.UserMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.MessageSource;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.client.RestTemplate;

import javax.persistence.EntityExistsException;
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
    public void save_withValidCredentialsAndUserRole_savesUserAndMakesUserRoleXref() {
        // Arrange
        Mockito.when(userRepository.findByUsername(ArgumentMatchers.anyString()))
                .thenReturn(Optional.empty());

        Mockito.when(userRepository.findByEmailAddress(ArgumentMatchers.anyString()))
                .thenReturn(Optional.empty());

        Mockito.when(userRoleRepository.findByRole(ArgumentMatchers.anyString()))
                .thenReturn(Optional.of(new UserRole()));

        User user = new User();
        user.setEmailAddress("random-address@trak.com");
        user.setVerificationCode((short)1234);

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
    public void verify_withNoMatchingUser_throwsEntityNotFoundException() {
        // Arrange
        Mockito.when(userRepository.findByUsername(ArgumentMatchers.anyString()))
                .thenReturn(Optional.empty());

        // Assert
        Assertions.assertThrows(EntityNotFoundException.class, () -> userService.verify("username", (short)1111));
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
        Assertions.assertThrows(InvalidUserException.class, () -> userService.verify("username", (short)1111));
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
        userService.verify("username", (short)1111);

        // Assert
        Mockito.verify(userRepository, Mockito.never())
                .save(ArgumentMatchers.any());
    }

    @Test
    public void verify_withNonVerifiedUserButIncorrectVerificationCode_throwsVerificationFailedException() {
        // Arrange
        User user = new User();
        user.setVerificationCode((short)1112);

        Mockito.when(userRepository.findByUsername(ArgumentMatchers.anyString()))
                .thenReturn(Optional.of(user));

        Mockito.when(authenticationService.isCurrentAuthenticatedUser(ArgumentMatchers.anyLong()))
                .thenReturn(true);

        // Assert
        Assertions.assertThrows(VerificationFailedException.class, () -> userService.verify("username", (short)1111));
    }

    @Test
    public void verify_withNonVerifiedUserWithCorrectVerificationCode_updatesUser() {
        // Arrange
        User user = new User();
        user.setVerificationCode((short)1111);

        Mockito.when(userRepository.findByUsername(ArgumentMatchers.anyString()))
                .thenReturn(Optional.of(user));

        Mockito.when(authenticationService.isCurrentAuthenticatedUser(ArgumentMatchers.anyLong()))
                .thenReturn(true);

        Mockito.when(userRepository.save(ArgumentMatchers.any()))
                .thenReturn(new User());

        // Act
        userService.verify("username", (short)1111);

        // Assert
        Mockito.verify(userRepository, Mockito.atMostOnce())
                .save(ArgumentMatchers.any());
    }
}

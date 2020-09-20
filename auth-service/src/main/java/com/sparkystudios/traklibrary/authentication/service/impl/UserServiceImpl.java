package com.sparkystudios.traklibrary.authentication.service.impl;

import com.sparkystudios.traklibrary.authentication.domain.User;
import com.sparkystudios.traklibrary.authentication.domain.UserRole;
import com.sparkystudios.traklibrary.authentication.repository.UserRepository;
import com.sparkystudios.traklibrary.authentication.repository.UserRoleRepository;
import com.sparkystudios.traklibrary.authentication.service.UserService;
import com.sparkystudios.traklibrary.authentication.service.dto.*;
import com.sparkystudios.traklibrary.authentication.service.event.OnChangePasswordEvent;
import com.sparkystudios.traklibrary.authentication.service.event.OnRecoveryNeededEvent;
import com.sparkystudios.traklibrary.authentication.service.event.OnVerificationNeededEvent;
import com.sparkystudios.traklibrary.authentication.service.exception.InvalidUserException;
import com.sparkystudios.traklibrary.authentication.service.mapper.UserMapper;
import com.sparkystudios.traklibrary.authentication.service.mapper.UserResponseMapper;
import com.sparkystudios.traklibrary.security.AuthenticationService;
import lombok.RequiredArgsConstructor;
import org.passay.CharacterRule;
import org.passay.EnglishCharacterData;
import org.passay.PasswordGenerator;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Random;
import java.util.stream.IntStream;

@RequiredArgsConstructor
@Service
public class UserServiceImpl implements UserService {

    private static final String NOT_FOUND_MESSAGE = "user.exception.not-found";
    private static final String EXISTING_USERNAME_MESSAGE = "user.error.existing-username";
    private static final String EXISTING_EMAIL_ADDRESS_MESSAGE = "user.error.existing-email-address";
    private static final String USER_ROLE_XREF_NOT_FOUND_MESSAGE = "user-role-xref.exception.not-found";
    private static final String NOT_EXISTENT_USERNAME_MESSAGE = "user.error.non-existent-username";
    private static final String INCORRECT_RECOVERY_TOKEN_MESSAGE = "user.error.incorrect-recovery-token";
    private static final String SAME_EMAIL_ADDRESS_MESSAGE = "user.error.same-email-address";
    private static final String INCORRECT_VERIFICATION_CODE_MESSAGE = "user.error.incorrect-verification-code";
    private static final String INVALID_USER_MESSAGE = "user.exception.invalid-user";

    private final UserRepository userRepository;
    private final UserRoleRepository userRoleRepository;
    private final UserMapper userMapper;
    private final UserResponseMapper userResponseMapper;
    private final MessageSource messageSource;
    private final PasswordEncoder passwordEncoder;
    private final ApplicationEventPublisher applicationEventPublisher;
    private final AuthenticationService authenticationService;

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) {
        Optional<User> user = userRepository.findByUsername(username);
        if (!user.isPresent()) {
            String errorMessage = messageSource
                    .getMessage(NOT_FOUND_MESSAGE, new Object[]{username}, LocaleContextHolder.getLocale());

            throw new UsernameNotFoundException(errorMessage);
        }

        return userMapper.userToUserDto(user.get());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public CheckedResponse<UserResponseDto> save(RegistrationRequestDto registrationRequestDto) {
        Optional<User> existingUsername = userRepository.findByUsername(registrationRequestDto.getUsername());
        // If the user already exists with the given username, don't save an identical one, throw an exception that it's already used.
        if (existingUsername.isPresent()) {
            String errorMessage = messageSource
                    .getMessage(EXISTING_USERNAME_MESSAGE, new Object[]{registrationRequestDto.getUsername()}, LocaleContextHolder.getLocale());

            return new CheckedResponse<>(null, errorMessage);
        }

        Optional<User> existingEmailAddress = userRepository.findByEmailAddress(registrationRequestDto.getEmailAddress());
        // If the email address is already in user, don't save an identical one.
        if (existingEmailAddress.isPresent()) {
            String errorMessage = messageSource
                    .getMessage(EXISTING_EMAIL_ADDRESS_MESSAGE, new Object[]{registrationRequestDto.getEmailAddress()}, LocaleContextHolder.getLocale());

            return new CheckedResponse<>(null, errorMessage);
        }

        // Create the link between the ROLE_USER and the new user.
        Optional<UserRole> userRole = userRoleRepository.findByRole("ROLE_USER");
        if (!userRole.isPresent()) {
            String errorMessage = messageSource
                    .getMessage(USER_ROLE_XREF_NOT_FOUND_MESSAGE, new Object[]{"ROLE_USER"}, LocaleContextHolder.getLocale());

            throw new EntityNotFoundException(errorMessage);
        }

        // Create a new user and assign it the ROLE_USER by default, but ensure it's not yet verified.
        User newUser = new User();
        newUser.setUsername(registrationRequestDto.getUsername());
        newUser.setEmailAddress(registrationRequestDto.getEmailAddress());
        newUser.setPassword(passwordEncoder.encode(registrationRequestDto.getPassword()));
        newUser.addUserRole(userRole.get());

        User user = userRepository.save(newUser);

        // Dispatch an event to generate the verification token and send the email.
        applicationEventPublisher
                .publishEvent(new OnVerificationNeededEvent(this, user.getUsername(), user.getEmailAddress()));

        return new CheckedResponse<>(userResponseMapper.userToUserResponseDto(user));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public CheckedResponse<UserResponseDto> update(RecoveryRequestDto recoveryRequestDto) {
        Optional<User> optionalUser = userRepository.findByUsername(recoveryRequestDto.getUsername());

        // If the user has provided an incorrect username, return an error message stating it can't be found.
        if (!optionalUser.isPresent()) {
            String errorMessage = messageSource
                    .getMessage(NOT_EXISTENT_USERNAME_MESSAGE, new Object[]{recoveryRequestDto.getUsername()}, LocaleContextHolder.getLocale());

            return new CheckedResponse<>(null, errorMessage);
        }

        User user = optionalUser.get();
        // If the user has no recovery token or the one provided doesn't match, fail the recovery process.
        if (user.getRecoveryToken() == null || !user.getRecoveryToken().equals(recoveryRequestDto.getRecoveryToken())) {
            String errorMessage = messageSource
                    .getMessage(INCORRECT_RECOVERY_TOKEN_MESSAGE, new Object[]{recoveryRequestDto.getRecoveryToken()}, LocaleContextHolder.getLocale());

            return new CheckedResponse<>(null, errorMessage);
        }

        // Recovery was successful, remove any recovery information and change their password.
        user.setRecoveryToken(null);
        user.setRecoveryTokenExpiryDate(null);
        user.setPassword(passwordEncoder.encode(recoveryRequestDto.getPassword()));

        // No need to re-verify, just return the new information.
        return new CheckedResponse<>(userResponseMapper.userToUserResponseDto(userRepository.save(user)));
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponseDto findByUsername(String username) {
        return userResponseMapper.userToUserResponseDto(getUserFromUsername(username));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteByUsername(String username) {
        User user = getUserFromUsername(username);

        // The user won't be null at this point, so we don't need to do any additional checking before deleting.
        // We need to remove its roles first before deleting otherwise it'll fail due to a foreign key constraint.
        user.getUserRoles().forEach(user::removeUserRole);
        user = userRepository.save(user);

        // Delete the user. There's no recovery from here.
        userRepository.deleteById(user.getId());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public String createVerificationCode(String username) {
        User user = userRepository.findByUsername(username)
                .orElse(null);

        if (user != null) {
            String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
            StringBuilder verificationCode = new StringBuilder();
            Random random = new SecureRandom();

            IntStream.range(0, 5).forEach(i -> verificationCode.append(chars.charAt(random.nextInt(chars.length()))));

            user.setVerified(false);
            user.setVerificationCode(verificationCode.toString());
            user.setVerificationExpiryDate(LocalDateTime.now());

            userRepository.save(user);

            return verificationCode.toString();
        }

        return "";
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public String createRecoveryToken(String username) {
        User user = userRepository.findByUsername(username)
                .orElse(null);

        if (user != null) {
            String recoveryToken = new PasswordGenerator().generatePassword(30,
                    new CharacterRule(EnglishCharacterData.UpperCase, 1),
                    new CharacterRule(EnglishCharacterData.LowerCase, 1),
                    new CharacterRule(EnglishCharacterData.Digit, 1));

            user.setRecoveryToken(recoveryToken);
            user.setRecoveryTokenExpiryDate(LocalDateTime.now());

            userRepository.save(user);

            return recoveryToken;
        }

        return "";
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public CheckedResponse<Boolean> verify(String username, String verificationCode) {
        User user = getUserFromUsername(username);

        // No point verifying for a user that's already verified.
        if (!user.isVerified()) {
            // If the verification code doesn't match, then the verification will have failed.
            if (user.getVerificationCode() == null || !user.getVerificationCode().equals(verificationCode)) {
                String errorMessage = messageSource
                        .getMessage(INCORRECT_VERIFICATION_CODE_MESSAGE, new Object[]{verificationCode, username}, LocaleContextHolder.getLocale());

                return new CheckedResponse<>(false, errorMessage);
            }

            // Update the verification state and persist the change.
            user.setVerified(true);
            user.setVerificationCode(null);
            user.setVerificationExpiryDate(null);

            userRepository.save(user);
        }

        return new CheckedResponse<>(true);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void reverify(String username) {
        User user = getUserFromUsername(username);

        // Resend the verification request to generate a new verification code and email.
        applicationEventPublisher
                .publishEvent(new OnVerificationNeededEvent(this, user.getUsername(), user.getEmailAddress()));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void requestRecovery(String emailAddress) {
        Optional<User> optionalUser = userRepository.findByEmailAddress(emailAddress);
        // The user has an email address registered with the system, process the reset request.
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();

            // Publish a reset password event to send an email.
            applicationEventPublisher
                    .publishEvent(new OnRecoveryNeededEvent(this, user.getEmailAddress(), user.getUsername()));
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void requestChangePassword(String username) {
        // Get the authenticated user associated with the given username. It'll throw an exception if they're
        // not authenticated.
        User user = getUserFromUsername(username);

        // Publish a change password event to send an email.
        applicationEventPublisher
                .publishEvent(new OnChangePasswordEvent(this, user.getEmailAddress(), username));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public CheckedResponse<Boolean> changePassword(String username, ChangePasswordRequestDto changePasswordRequestDto) {
        // Get the authenticated user associated with the given username. It'll throw an exception if they're
        // not authenticated.
        User user = getUserFromUsername(username);

        // If the user has no recovery token or the one provided doesn't match, fail the recovery process.
        if (user.getRecoveryToken() == null || !user.getRecoveryToken().equals(changePasswordRequestDto.getRecoveryToken())) {
            String errorMessage = messageSource
                    .getMessage(INCORRECT_RECOVERY_TOKEN_MESSAGE, new Object[]{changePasswordRequestDto.getRecoveryToken()}, LocaleContextHolder.getLocale());

            return new CheckedResponse<>(false, errorMessage);
        }

        // Reset was successful, remove any recovery information and change their password.
        user.setRecoveryToken(null);
        user.setRecoveryTokenExpiryDate(null);
        user.setPassword(passwordEncoder.encode(changePasswordRequestDto.getNewPassword()));

        userRepository.save(user);

        // No need to re-verify, just return the new information.
        return new CheckedResponse<>(true);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public CheckedResponse<Boolean> changeEmailAddress(String username, String emailAddress) {
        // Get the authenticated user associated with the given username. It'll throw an exception if they're
        // not authenticated.
        User user = getUserFromUsername(username);

        if (user.getEmailAddress().equals(emailAddress)) {
            String errorMessage = messageSource
                    .getMessage(SAME_EMAIL_ADDRESS_MESSAGE, new Object[]{}, LocaleContextHolder.getLocale());

            return new CheckedResponse<>(false, errorMessage);
        }

        // Update the user with the new email and persist it.
        user.setEmailAddress(emailAddress);
        userRepository.save(user);

        // Will need to re-generate the verification email as the users information has changed.
        applicationEventPublisher
                .publishEvent(new OnVerificationNeededEvent(this, username, emailAddress));

        return new CheckedResponse<>(true);
    }

    private User getUserFromUsername(String username) {
        Optional<User> optionalUser = userRepository.findByUsername(username);
        // Can't verify a user if it doesn't exist.
        if (!optionalUser.isPresent()) {
            String errorMessage = messageSource
                    .getMessage(NOT_FOUND_MESSAGE, new Object[]{username}, LocaleContextHolder.getLocale());

            throw new EntityNotFoundException(errorMessage);
        }

        User user = optionalUser.get();

        if (!authenticationService.isCurrentAuthenticatedUser(user.getId())) {
            String errorMessage = messageSource
                    .getMessage(INVALID_USER_MESSAGE, new Object[]{}, LocaleContextHolder.getLocale());

            throw new InvalidUserException(errorMessage);
        }

        return user;
    }
}

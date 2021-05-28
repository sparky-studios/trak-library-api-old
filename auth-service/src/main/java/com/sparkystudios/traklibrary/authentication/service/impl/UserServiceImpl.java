package com.sparkystudios.traklibrary.authentication.service.impl;

import com.sparkystudios.traklibrary.authentication.domain.User;
import com.sparkystudios.traklibrary.authentication.domain.UserRole;
import com.sparkystudios.traklibrary.authentication.repository.UserRepository;
import com.sparkystudios.traklibrary.authentication.repository.UserRoleRepository;
import com.sparkystudios.traklibrary.authentication.service.UserService;
import com.sparkystudios.traklibrary.authentication.service.dto.*;
import com.sparkystudios.traklibrary.authentication.service.event.ChangePasswordEvent;
import com.sparkystudios.traklibrary.authentication.service.event.RecoveryEvent;
import com.sparkystudios.traklibrary.authentication.service.event.VerificationEvent;
import com.sparkystudios.traklibrary.authentication.service.exception.InvalidUserException;
import com.sparkystudios.traklibrary.authentication.service.mapper.UserMapper;
import com.sparkystudios.traklibrary.authentication.service.mapper.UserResponseMapper;
import com.sparkystudios.traklibrary.security.AuthenticationService;
import lombok.RequiredArgsConstructor;
import org.passay.CharacterRule;
import org.passay.EnglishCharacterData;
import org.passay.PasswordGenerator;
import org.springframework.cloud.stream.function.StreamBridge;
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

    private static final String EMAIL_VERIFICATION_DESTINATION = "trak-email-verification";
    private static final String EMAIL_RECOVERY_DESTINATION = "trak-email-recovery";
    private static final String EMAIL_CHANGE_PASSWORD_DESTINATION = "trak-email-change-password";

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
    private final StreamBridge streamBridge;
    private final AuthenticationService authenticationService;

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) {
        Optional<User> user = userRepository.findByUsername(username);
        if (user.isEmpty()) {
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
        if (userRole.isEmpty()) {
            String errorMessage = messageSource
                    .getMessage(USER_ROLE_XREF_NOT_FOUND_MESSAGE, new Object[]{"ROLE_USER"}, LocaleContextHolder.getLocale());

            throw new EntityNotFoundException(errorMessage);
        }

        // Create a new user and assign it the ROLE_USER by default, but ensure it's not yet verified.
        var newUser = new User();
        newUser.setUsername(registrationRequestDto.getUsername());
        newUser.setEmailAddress(registrationRequestDto.getEmailAddress());
        newUser.setPassword(passwordEncoder.encode(registrationRequestDto.getPassword()));
        newUser.setUserRole(userRole.get());
        newUser.setVerified(false);
        newUser.setVerificationCode(createVerificationCode());
        newUser.setVerificationExpiryDate(LocalDateTime.now().plusDays(1));

        var user = userRepository.save(newUser);

        // Dispatch an event to generate the verification token and send the email.
        streamBridge.send(EMAIL_VERIFICATION_DESTINATION,
                new VerificationEvent(user.getUsername(), user.getEmailAddress(), user.getVerificationCode()));

        return new CheckedResponse<>(userResponseMapper.userToUserResponseDto(user));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public CheckedResponse<UserResponseDto> update(RecoveryRequestDto recoveryRequestDto) {
        Optional<User> optionalUser = userRepository.findByUsername(recoveryRequestDto.getUsername());

        // If the user has provided an incorrect username, return an error message stating it can't be found.
        if (optionalUser.isEmpty()) {
            String errorMessage = messageSource
                    .getMessage(NOT_EXISTENT_USERNAME_MESSAGE, new Object[]{recoveryRequestDto.getUsername()}, LocaleContextHolder.getLocale());

            return new CheckedResponse<>(null, errorMessage);
        }

        var user = optionalUser.get();
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
    @Transactional(rollbackFor = Exception.class)
    public void deleteByUsername(String username) {
        var user = getUserFromUsername(username);
        // Delete the user. There's no recovery from here.
        userRepository.deleteById(user.getId());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public CheckedResponse<Boolean> verify(String username, String verificationCode) {
        var user = getUserFromUsername(username);

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
        var user = getUserFromUsername(username);
        user.setVerified(false);
        user.setVerificationCode(createVerificationCode());
        user.setVerificationExpiryDate(LocalDateTime.now().plusDays(1));

        user = userRepository.save(user);

        // Resend the verification request to generate a new email.
        streamBridge.send(EMAIL_VERIFICATION_DESTINATION,
                new VerificationEvent(user.getUsername(), user.getEmailAddress(), user.getVerificationCode()));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void requestRecovery(String emailAddress) {
        Optional<User> optionalUser = userRepository.findByEmailAddress(emailAddress);
        // The user has an email address registered with the system, process the reset request.
        if (optionalUser.isPresent()) {
            var user = optionalUser.get();
            user.setRecoveryToken(createRecoveryToken());
            user.setRecoveryTokenExpiryDate(LocalDateTime.now().plusDays(1));

            user = userRepository.save(user);

            // Publish a reset password event to send an email.
            streamBridge.send(EMAIL_RECOVERY_DESTINATION,
                    new RecoveryEvent(user.getUsername(), user.getEmailAddress(), user.getRecoveryToken()));
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void requestChangePassword(String username) {
        // Get the authenticated user associated with the given username. It'll throw an exception if they're
        // not authenticated.
        var user = getUserFromUsername(username);
        user.setRecoveryToken(createRecoveryToken());
        user.setRecoveryTokenExpiryDate(LocalDateTime.now().plusDays(1));

        user = userRepository.save(user);

        // Publish a change password event to send an email.
        streamBridge.send(EMAIL_CHANGE_PASSWORD_DESTINATION,
                new ChangePasswordEvent(user.getUsername(), user.getEmailAddress(), user.getRecoveryToken()));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public CheckedResponse<Boolean> changePassword(String username, ChangePasswordRequestDto changePasswordRequestDto) {
        // Get the authenticated user associated with the given username. It'll throw an exception if they're
        // not authenticated.
        var user = getUserFromUsername(username);

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
        var user = getUserFromUsername(username);

        if (user.getEmailAddress().equals(emailAddress)) {
            String errorMessage = messageSource
                    .getMessage(SAME_EMAIL_ADDRESS_MESSAGE, new Object[]{}, LocaleContextHolder.getLocale());

            return new CheckedResponse<>(false, errorMessage);
        }

        // Update the user with the new email and persist it.
        user.setEmailAddress(emailAddress);
        user.setVerified(false);
        user.setVerificationCode(createVerificationCode());
        user.setVerificationExpiryDate(LocalDateTime.now().plusDays(1));

        user = userRepository.save(user);

        // Will need to re-generate the verification email as the users information has changed.
        streamBridge.send(EMAIL_VERIFICATION_DESTINATION,
                new VerificationEvent(user.getUsername(), user.getEmailAddress(), user.getVerificationCode()));

        return new CheckedResponse<>(true);
    }

    private User getUserFromUsername(String username) {
        Optional<User> optionalUser = userRepository.findByUsername(username);
        // Can't verify a user if it doesn't exist.
        if (optionalUser.isEmpty()) {
            String errorMessage = messageSource
                    .getMessage(NOT_FOUND_MESSAGE, new Object[]{username}, LocaleContextHolder.getLocale());

            throw new EntityNotFoundException(errorMessage);
        }

        var user = optionalUser.get();

        if (!authenticationService.isCurrentAuthenticatedUser(user.getId())) {
            String errorMessage = messageSource
                    .getMessage(INVALID_USER_MESSAGE, new Object[]{}, LocaleContextHolder.getLocale());

            throw new InvalidUserException(errorMessage);
        }

        return user;
    }

    private String createVerificationCode() {
        var chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        var verificationCode = new StringBuilder();
        Random random = new SecureRandom();

        IntStream.range(0, 5).forEach(i -> verificationCode.append(chars.charAt(random.nextInt(chars.length()))));

        return verificationCode.toString();
    }

    private String createRecoveryToken() {
        return new PasswordGenerator().generatePassword(30,
                new CharacterRule(EnglishCharacterData.UpperCase, 1),
                new CharacterRule(EnglishCharacterData.LowerCase, 1),
                new CharacterRule(EnglishCharacterData.Digit, 1));
    }
}

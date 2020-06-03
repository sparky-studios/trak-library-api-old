package com.sparky.trak.authentication.service.impl;

import com.sparky.trak.authentication.domain.User;
import com.sparky.trak.authentication.domain.UserRole;
import com.sparky.trak.authentication.domain.UserRoleXref;
import com.sparky.trak.authentication.repository.UserRepository;
import com.sparky.trak.authentication.repository.UserRoleRepository;
import com.sparky.trak.authentication.repository.UserRoleXrefRepository;
import com.sparky.trak.authentication.service.AuthenticationService;
import com.sparky.trak.authentication.service.UserService;
import com.sparky.trak.authentication.service.dto.CheckedResponse;
import com.sparky.trak.authentication.service.dto.RegistrationRequestDto;
import com.sparky.trak.authentication.service.dto.UserResponseDto;
import com.sparky.trak.authentication.service.event.OnVerificationNeededEvent;
import com.sparky.trak.authentication.service.exception.InvalidUserException;
import com.sparky.trak.authentication.service.mapper.UserMapper;
import com.sparky.trak.authentication.service.mapper.UserResponseMapper;
import lombok.RequiredArgsConstructor;
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

    private final UserRepository userRepository;
    private final UserRoleRepository userRoleRepository;
    private final UserRoleXrefRepository userRoleXrefRepository;
    private final UserMapper userMapper;
    private final UserResponseMapper userResponseMapper;
    private final MessageSource messageSource;
    private final PasswordEncoder passwordEncoder;
    private final ApplicationEventPublisher applicationEventPublisher;
    private final AuthenticationService authenticationService;

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<User> user = userRepository.findByUsername(username);
        if (!user.isPresent()) {
            String errorMessage = messageSource
                    .getMessage("user.exception.not-found", new Object[]{username}, LocaleContextHolder.getLocale());

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
                    .getMessage("user.error.existing-username", new Object[]{registrationRequestDto.getUsername()}, LocaleContextHolder.getLocale());

            return new CheckedResponse<>(null, errorMessage);
        }

        Optional<User> existingEmailAddress = userRepository.findByEmailAddress(registrationRequestDto.getEmailAddress());
        // If the email address is already in user, don't save an identical one.
        if (existingEmailAddress.isPresent()) {
            String errorMessage = messageSource
                    .getMessage("user.error.existing-email-address", new Object[]{registrationRequestDto.getEmailAddress()}, LocaleContextHolder.getLocale());

            return new CheckedResponse<>(null, errorMessage);
        }

        // Create the link between the ROLE_USER and the new user.
        Optional<UserRole> userRole = userRoleRepository.findByRole("ROLE_USER");
        if (!userRole.isPresent()) {
            String errorMessage = messageSource
                    .getMessage("user-role-xref.exception.not-found", new Object[]{"ROLE_USER"}, LocaleContextHolder.getLocale());

            throw new EntityNotFoundException(errorMessage);
        }

        // Create a new user and assign it the ROLE_USER by default, but ensure it's not yet verified.
        User newUser = new User();
        newUser.setUsername(registrationRequestDto.getUsername());
        newUser.setEmailAddress(registrationRequestDto.getEmailAddress());
        newUser.setPassword(passwordEncoder.encode(registrationRequestDto.getPassword()));

        User user = userRepository.save(newUser);

        // Create a user role xref between the user role and the new user.
        UserRoleXref userRoleXref = new UserRoleXref();
        userRoleXref.setUserId(user.getId());
        userRoleXref.setUserRoleId(userRole.get().getId());

        userRoleXrefRepository.save(userRoleXref);

        // Dispatch an event to generate the verification token and send the email.
        applicationEventPublisher
                .publishEvent(new OnVerificationNeededEvent(this, user.getUsername(), user.getEmailAddress()));

        return new CheckedResponse<>(userResponseMapper.userToUserResponseDto(user), "");
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponseDto findByUsername(String username) {
        return userResponseMapper.userToUserResponseDto(getUserFromUsername(username));
    }

    @Override
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
    public CheckedResponse<Boolean> verify(String username, String verificationCode) {
        User user = getUserFromUsername(username);

        // No point verifying for a user that's already verified.
        if (!user.isVerified()) {
            // If the verification code doesn't match, then the verification will have failed.
            if (user.getVerificationCode() == null || !user.getVerificationCode().equals(verificationCode)) {
                String errorMessage = messageSource
                        .getMessage("user.error.incorrect-verification-code", new Object[]{verificationCode, username}, LocaleContextHolder.getLocale());

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
    public void reverify(String username) {
        User user = getUserFromUsername(username);

        // Resend the verification request to generate a new verification code and email.
        applicationEventPublisher
                .publishEvent(new OnVerificationNeededEvent(this, user.getUsername(), user.getEmailAddress()));
    }

    private User getUserFromUsername(String username) {
        Optional<User> optionalUser = userRepository.findByUsername(username);
        // Can't verify a user if it doesn't exist.
        if (!optionalUser.isPresent()) {
            String errorMessage = messageSource
                    .getMessage("user.exception.not-found", new Object[]{username}, LocaleContextHolder.getLocale());

            throw new EntityNotFoundException(errorMessage);
        }

        User user = optionalUser.get();

        if (!authenticationService.isCurrentAuthenticatedUser(user.getId())) {
            String errorMessage = messageSource
                    .getMessage("user.exception.invalid-user", new Object[]{}, LocaleContextHolder.getLocale());

            throw new InvalidUserException(errorMessage);
        }

        return user;
    }
}

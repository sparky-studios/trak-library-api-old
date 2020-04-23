package com.sparky.trak.authentication.service.impl;

import com.sparky.trak.authentication.domain.User;
import com.sparky.trak.authentication.domain.UserRole;
import com.sparky.trak.authentication.domain.UserRoleXref;
import com.sparky.trak.authentication.repository.UserRepository;
import com.sparky.trak.authentication.repository.UserRoleRepository;
import com.sparky.trak.authentication.repository.UserRoleXrefRepository;
import com.sparky.trak.authentication.service.UserService;
import com.sparky.trak.authentication.service.dto.RegistrationRequestDto;
import com.sparky.trak.authentication.service.dto.UserCredentialsDto;
import com.sparky.trak.authentication.service.dto.UserDto;
import com.sparky.trak.authentication.service.exception.VerificationFailedException;
import com.sparky.trak.authentication.service.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityExistsException;
import javax.persistence.EntityNotFoundException;
import java.security.SecureRandom;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserRoleRepository userRoleRepository;
    private final UserRoleXrefRepository userRoleXrefRepository;
    private final UserMapper userMapper;
    private final MessageSource messageSource;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<User> user = userRepository.findByUsername(username);
        if (!user.isPresent()) {
            String errorMessage = messageSource
                    .getMessage("user.exception.not-found", new Object[] {username}, LocaleContextHolder.getLocale());

            throw new UsernameNotFoundException(errorMessage);
        }

        return userMapper.userToUserDto(user.get());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void save(RegistrationRequestDto registrationRequestDto) {
        Optional<User> existingUsername = userRepository.findByUsername(registrationRequestDto.getUsername());
        // If the user already exists with the given username, don't save an identical one, throw an exception that it's already used.
        if (existingUsername.isPresent()) {
            String errorMessage = messageSource
                    .getMessage("user.exception.username-already-in-use", new Object[] {registrationRequestDto.getUsername()}, LocaleContextHolder.getLocale());

            throw new EntityExistsException(errorMessage);
        }

        Optional<User> existingEmailAddress = userRepository.findByEmailAddress(registrationRequestDto.getEmailAddress());
        // If the email address is already in user, don't save an identical one.
        if (existingEmailAddress.isPresent()) {
            String errorMessage = messageSource
                    .getMessage("user.exception.email-address-already-in-use", new Object[] {registrationRequestDto.getEmailAddress()}, LocaleContextHolder.getLocale());

            throw new EntityExistsException(errorMessage);
        }

        // Create the link between the ROLE_USER and the new user.
        Optional<UserRole> userRole = userRoleRepository.findByRole("ROLE_USER");
        if (!userRole.isPresent()) {
            String errorMessage = messageSource
                    .getMessage("user-role-xref.exception.not-found", new Object[] {"ROLE_USER"}, LocaleContextHolder.getLocale());

            throw new EntityNotFoundException(errorMessage);
        }

        // Create a new user and assign it the ROLE_USER by default, but ensure it's not yet validated.
        User newUser = new User();
        newUser.setUsername(registrationRequestDto.getUsername());
        newUser.setEmailAddress(registrationRequestDto.getEmailAddress());
        newUser.setPassword(passwordEncoder.encode(registrationRequestDto.getPassword()));
        // Generate a random verification code between 1000 and 9999.
        newUser.setVerificationCode((short)(new SecureRandom().nextInt((9999 - 1000) + 1) + 1000));

        User user = userRepository.save(newUser);

        // Create a user role xref between the user role and the new user.
        UserRoleXref userRoleXref = new UserRoleXref();
        userRoleXref.setUserId(user.getId());
        userRoleXref.setUserRoleId(userRole.get().getId());

        userRoleXrefRepository.save(userRoleXref);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isVerified(String username) {
        Optional<User> optionalUser = userRepository.findByUsername(username);
        // Can't verify a user if it doesn't exist.
        if (!optionalUser.isPresent()) {
            String errorMessage = messageSource
                    .getMessage("user.exception.not-found", new Object[] {username}, LocaleContextHolder.getLocale());

            throw new EntityNotFoundException(errorMessage);
        }

        return optionalUser.get().isVerified();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void verify(String username, short verificationCode) {
        Optional<User> optionalUser = userRepository.findByUsername(username);
        // Can't verify a user if it doesn't exist.
        if (!optionalUser.isPresent()) {
            String errorMessage = messageSource
                    .getMessage("user.exception.not-found", new Object[] {username}, LocaleContextHolder.getLocale());

            throw new EntityNotFoundException(errorMessage);
        }

        User user = optionalUser.get();
        // No point verifying for a user that's already verified.
        if (!user.isVerified()) {
            // If the verification code doesn't match, then the verification will have failed.
            if (user.getVerificationCode() != verificationCode) {
                String errorMessage = messageSource
                        .getMessage("user.exception.verification-failed", new Object[] {verificationCode, username}, LocaleContextHolder.getLocale());

                throw new VerificationFailedException(errorMessage);
            }

            // Update the verification state and persist the change.
            user.setVerified(true);
            user.setVerificationCode(null);

            userRepository.save(user);
        }
    }
}

package com.sparkystudios.traklibrary.authentication.service.impl;

import com.sparkystudios.traklibrary.authentication.service.TwoFactorAuthenticationService;
import com.sparkystudios.traklibrary.authentication.service.UserService;
import com.sparkystudios.traklibrary.authentication.service.dto.RegistrationResponseDto;
import com.sparkystudios.traklibrary.authentication.service.dto.TwoFactorAuthenticationRequestDto;
import com.sparkystudios.traklibrary.authentication.service.dto.UserDto;
import com.sparkystudios.traklibrary.authentication.service.dto.UserResponseDto;
import com.sparkystudios.traklibrary.authentication.service.mapper.UserMapper;
import com.sparkystudios.traklibrary.authentication.service.mapper.UserResponseMapper;
import dev.samstevens.totp.code.CodeVerifier;
import dev.samstevens.totp.code.HashingAlgorithm;
import dev.samstevens.totp.exceptions.QrGenerationException;
import dev.samstevens.totp.qr.QrData;
import dev.samstevens.totp.qr.ZxingPngQrGenerator;
import dev.samstevens.totp.secret.SecretGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityExistsException;

@Service
@RequiredArgsConstructor
public class TwoFactorAuthenticationServiceImpl implements TwoFactorAuthenticationService {

    private static final String TWO_FACTOR_AUTHENTICATION_EXISTS = "two-factor-authentication.exception.exists";
    private static final String BAD_CREDENTIALS = "authentication.exception.bad-2fa-credentials";
    private static final String INVALID_QR_CODE = "two-factor-authentication.error.invalid-qr-code";

    private final UserService userService;
    private final SecretGenerator secretGenerator;
    private final ZxingPngQrGenerator zxingPngQrGenerator;
    private final CodeVerifier codeVerifier;
    private final MessageSource messageSource;
    private final UserResponseMapper userResponseMapper;
    private final UserMapper userMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public RegistrationResponseDto createTwoFactorAuthenticationSecret(long id) {
        // Retrieve the user to try to generate a two-factor authentication secret for.
        UserDto userDto = userService.findById(id);

        // If it's already enabled, don't allow the code to be re-generated until the 2FA has been disabled.
        if (userDto.isUsingTwoFactorAuthentication()) {
            String errorMessage = messageSource
                    .getMessage(TWO_FACTOR_AUTHENTICATION_EXISTS, new Object[] {}, LocaleContextHolder.getLocale());

            throw new EntityExistsException(errorMessage);
        }

        // Generate a new secret for the user.
        userDto.setTwoFactorAuthenticationSecret(secretGenerator.generate());
        userDto.setUsingTwoFactorAuthentication(false);

        QrData data = new QrData.Builder()
                .label(userDto.getUsername())
                .secret(userDto.getTwoFactorAuthenticationSecret())
                .issuer("Trak Library")
                .algorithm(HashingAlgorithm.SHA1)
                .digits(6)
                .period(30)
                .build();

        // Generate the QR code data for display on the client.
        RegistrationResponseDto registrationResponseDto = new RegistrationResponseDto();
        registrationResponseDto.setUserId(userDto.getId());
        try {
            registrationResponseDto.setQrData(zxingPngQrGenerator.generate(data));
        } catch (QrGenerationException e) {
            String errorMessage = messageSource
                    .getMessage(INVALID_QR_CODE, new Object[]{}, LocaleContextHolder.getLocale());

            throw new IllegalStateException(errorMessage);
        }

        return registrationResponseDto;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public UserResponseDto enable(long id, TwoFactorAuthenticationRequestDto twoFactorAuthenticationRequestDto) {
        // Retrieve the user to try and enable two-factor authentication for.
        UserDto userDto = userService.findById(id);

        // Only process a check if they haven't already enabled 2FA.
        if (!userDto.isUsingTwoFactorAuthentication()) {

            // Check if the code they have passed up is valid.
            if (!codeVerifier.isValidCode(userDto.getTwoFactorAuthenticationSecret(), twoFactorAuthenticationRequestDto.getCode())) {
                String errorMessage = messageSource
                        .getMessage(BAD_CREDENTIALS, new Object[] {}, LocaleContextHolder.getLocale());

                throw new BadCredentialsException(errorMessage);
            }

            userDto.setUsingTwoFactorAuthentication(true);
            userDto = userService.update(userDto);
        }

        return userResponseMapper.fromUser(userMapper.toUser(userDto));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public UserResponseDto disable(long id) {
        // Retrieve the user to try and disable two-factor authentication for.
        UserDto userDto = userService.findById(id);

        // Disable the two settings associated with 2FA.
        userDto.setTwoFactorAuthenticationSecret(null);
        userDto.setUsingTwoFactorAuthentication(false);

        // Update and map the response.
        return userResponseMapper.fromUser(userMapper.toUser(userService.update(userDto)));
    }
}

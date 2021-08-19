package com.sparkystudios.traklibrary.authentication.service;

import com.sparkystudios.traklibrary.authentication.service.dto.RegistrationResponseDto;
import com.sparkystudios.traklibrary.authentication.service.dto.TwoFactorAuthenticationRequestDto;
import com.sparkystudios.traklibrary.authentication.service.dto.UserResponseDto;

public interface TwoFactorAuthenticationService {

    /**
     * Given the ID of a {@link com.sparkystudios.traklibrary.authentication.domain.User}, this method will
     * create a new two-factor authentication secret for the given user. The secret will only be created for a
     * user if it is not already been created for the given user.
     *
     * An exception will be thrown if attempting to create a new authentication secret for a user
     * that already has a secret created.
     *
     * @param id The ID of the user to create a two-factor authentication secret for.
     *
     * @return A {@link RegistrationResponseDto} containing the generated QR code for the secret.
     */
    RegistrationResponseDto createTwoFactorAuthenticationSecret(long id);

    /**
     * Given the ID of a {@link com.sparkystudios.traklibrary.authentication.domain.User}, this method will attempt
     * to enable 2FA for the given account, if the account has not yet already had two-factor authentication enabled
     * and the code they have provided matches the 2FA generated credentials.
     *
     * If the code entered does not match the 2FA secret code, then the method will return a {@link org.springframework.security.authentication.BadCredentialsException}.
     * If the code is valid or the user already has 2FA enabled, a {@link UserResponseDto} will be returned with basic
     * user data.
     *
     * Similar to every other method that edits user data, the user can only edit their own data, unless they have
     * elevated privileges.
     *
     * @param id The ID of the {@link com.sparkystudios.traklibrary.authentication.domain.User} to enable 2FA for.
     * @param twoFactorAuthenticationRequestDto The {@link TwoFactorAuthenticationRequestDto} containing the 2FA code.
     *
     * @return A {@link UserResponseDto} on successful 2FA enabling.
     */
    UserResponseDto enable(long id, TwoFactorAuthenticationRequestDto twoFactorAuthenticationRequestDto);

    /**
     * Given the ID of a {@link com.sparkystudios.traklibrary.authentication.domain.User}, this method will
     * attempt to disable 2FA for the given account, which merely involves removing the authentication secret and specifying
     * that they are no longer using 2FA for authentication.
     *
     * Similar to every other method that edits user data, the user can only edit their own data, unless they have
     * elevated privileges.
     *
     * @param id The ID of the {@link com.sparkystudios.traklibrary.authentication.domain.User} to disable 2FA for.
     *
     * @return A {@link UserResponseDto} on successful 2FA disabling.
     */
    UserResponseDto disable(long id);
}

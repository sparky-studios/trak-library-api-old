package com.sparkystudios.traklibrary.authentication.server.controller;

import com.sparkystudios.traklibrary.authentication.service.TwoFactorAuthenticationService;
import com.sparkystudios.traklibrary.authentication.service.dto.RegistrationResponseDto;
import com.sparkystudios.traklibrary.authentication.service.dto.TwoFactorAuthenticationRequestDto;
import com.sparkystudios.traklibrary.authentication.service.dto.UserResponseDto;
import com.sparkystudios.traklibrary.security.annotation.AllowedForUser;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping(value = "/users/{id}/2fa", produces = "application/vnd.sparkystudios.traklibrary+json;version=1.0")
public class TwoFactorAuthenticationController {

    private final TwoFactorAuthenticationService twoFactorAuthenticationService;

    /**
     * End-point that is used to create a new two-factor authentication secret for the given
     * user. The secret will only be created for a user if it is not already been created for
     * the given user.
     *
     * An exception will be thrown if attempting to create a new authentication secret for a user
     * that already has a secret created.
     *
     * @param id The ID of the user to create a two-factor authentication secret for.
     *
     * @return A {@link RegistrationResponseDto} containing the generated QR code for the secret.
     */
    @AllowedForUser
    @PostMapping
    public RegistrationResponseDto createTwoFactorAuthenticationSecret(@PathVariable long id) {
        return twoFactorAuthenticationService.createTwoFactorAuthenticationSecret(id);
    }

    /**
     * End-point that is used to enable two-factor authentication for the user that matches the given ID.
     * Two-factor authentication will only be enabled for the given user if it has not yet been enabled,
     * if it is already configured for the user, it will just return the user data without making any
     * additional changes.
     *
     * @param id The ID of the user to enable two-factor authentication for.
     * @param twoFactorAuthenticationRequestDto The {@link TwoFactorAuthenticationRequestDto} request containing the 2FA code.
     *
     * @return A {@link UserResponseDto} representing the altered user.
     */
    @AllowedForUser
    @PutMapping
    public UserResponseDto enable(@PathVariable long id, @RequestBody @Validated TwoFactorAuthenticationRequestDto twoFactorAuthenticationRequestDto) {
        return twoFactorAuthenticationService.enable(id, twoFactorAuthenticationRequestDto);
    }

    /**
     * End-point that is used to disable two-factor authentication for the given user that matches the given ID.
     * Regardless of whether two-factor authentication was configured for the given account, it will still reset
     * the values that specify that it is disabled for the given account.
     *
     * @param id The ID of the user to disable two-factor authentication for.
     *
     * @return A {@link UserResponseDto} representing the altered user.
     */
    @AllowedForUser
    @DeleteMapping
    public UserResponseDto disable(@PathVariable long id) {
        return twoFactorAuthenticationService.disable(id);
    }
}

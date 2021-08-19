package com.sparkystudios.traklibrary.authentication.service.dto;

import lombok.Data;

import javax.validation.constraints.NotEmpty;

/**
 * A simple POJO that is used to represent the request body that is passed to the authorization request when the
 * user in question has two-factor authentication enabled on their account and they have already requested a JWT
 * with a username and password, which cannot access the API. Unlike other DTO's within the API, no validation
 * is done on this DTO, the reason for this is the fact that if credentials are incorrect, authentication will not occur
 * and no JWT will be returned.
 *
 * @since 0.1.0
 * @author Sparky Studios
 */
@Data
public class TwoFactorAuthenticationRequestDto {

    @NotEmpty(message = "{two-factor-authentication-request.validation.code.not-empty}")
    private String code;
}

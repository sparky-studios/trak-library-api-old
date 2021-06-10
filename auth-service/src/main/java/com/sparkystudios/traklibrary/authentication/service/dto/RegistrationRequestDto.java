package com.sparkystudios.traklibrary.authentication.service.dto;

import com.sparkystudios.traklibrary.authentication.domain.User;
import com.sparkystudios.traklibrary.authentication.service.validation.ValidPassword;
import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;

/**
 * The {@link RegistrationRequestDto} is a simple POJO DTO that is used by the request that is responsible for creating
 * and registering new {@link User}'s to the underlying persistence layer. Its purpose
 * is to boil down the information needed for registration down to the bare essential fields, and only expose the information
 * that is strictly needed to create a new {@link User}.
 *
 * Similar to all other DTO's within the API, it contains validation to ensure that the information provided to the end-points
 * is valid and correctly formatted before {@link User} creation.
 */
@Data
public class RegistrationRequestDto {

    @NotEmpty(message = "{registration-request.validation.username.not-empty}")
    private String username;

    @NotEmpty(message = "{registration-request.validation.email-address.not-empty}")
    @Email(message = "{registration-request.validation.email-address.invalid}")
    private String emailAddress;

    @ValidPassword(message = "{registration-request.validation.password.invalid}")
    private String password;

    private boolean useMultiFactorAuthentication;
}

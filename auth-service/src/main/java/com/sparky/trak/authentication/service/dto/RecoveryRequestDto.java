package com.sparky.trak.authentication.service.dto;

import com.sparky.trak.authentication.service.validation.ValidPassword;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

/**
 * The {@link RecoveryRequestDto} is a simple POJO DTO that is used by the request that is responsible for recovering an
 * account after the user forgot the current password assigned to it. Its purpose is to boil down the information needed
 * for re-registration and recovery down to the bare essential fields, and only expose the information that is strictly
 * needed to change the password of an already registered {@link com.sparky.trak.authentication.domain.User}.
 *
 * Similar to all other DTO's within the API, it contains validation to ensure that the information provided to the end-points
 * is valid and correctly formatted before {@link com.sparky.trak.authentication.domain.User} updating.
 */
@Data
public class RecoveryRequestDto {

    @NotEmpty(message = "{recovery-request.validation.username.not-empty}")
    private String username;

    @NotEmpty(message = "{recovery-request.validation.recovery-token.not-empty}")
    @Size(min = 30, max = 30, message = "{recovery-request.validation.recovery-token.size}")
    private String recoveryToken;

    @ValidPassword(message = "{recovery-request.validation.password.invalid}")
    private String password;
}

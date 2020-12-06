package com.sparkystudios.traklibrary.email.service.dto;

import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

@Data
public class EmailRecoveryRequestDto {

    @Email(message = "{email-recovery-request.email-address.not-valid}")
    private String emailAddress;

    @NotEmpty(message = "{email-recovery-request.recovery-token.not-empty}")
    @Size(min = 30, max = 30, message = "{email-recovery-request.recovery-token.size}")
    private String recoveryToken;
}

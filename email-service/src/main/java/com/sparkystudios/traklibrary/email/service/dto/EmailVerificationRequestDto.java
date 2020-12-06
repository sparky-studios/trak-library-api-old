package com.sparkystudios.traklibrary.email.service.dto;

import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

@Data
public class EmailVerificationRequestDto {

    @Email(message = "{email-verification-request.email-address.not-valid}")
    private String emailAddress;

    @NotEmpty(message = "{email-verification-request.verification-code.not-empty}")
    @Size(min = 5, max = 5, message = "{email-verification-request.verification-code.size}")
    private String verificationCode;
}

package com.sparkystudios.traklibrary.authentication.service.dto;

import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;

@Data
public class ChangeEmailAddressRequestDto {

    @NotEmpty(message = "{change-email-address-request.validation.recovery-token.not-empty}")
    @Email(message = "{change-email-address-request.validation.email-address.invalid}")
    public String emailAddress;
}

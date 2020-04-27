package com.sparky.trak.authentication.service.dto;

import com.sparky.trak.authentication.service.validation.ValidPassword;
import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;

@Data
public class RegistrationRequestDto {

    @NotEmpty(message = "{registration-request.validation.username.not-empty}")
    private String username;

    @Email(message = "{registration-request.validation.email-address.invalid}")
    private String emailAddress;

    @ValidPassword(message = "{registration-request.validation.password.invalid}")
    private String password;
}

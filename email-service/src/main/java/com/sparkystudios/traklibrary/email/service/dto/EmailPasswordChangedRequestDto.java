package com.sparkystudios.traklibrary.email.service.dto;

import lombok.Data;

import javax.validation.constraints.Email;

@Data
public class EmailPasswordChangedRequestDto {

    @Email(message = "{email-recovery-request.email-address.not-valid}")
    private String emailAddress;
}

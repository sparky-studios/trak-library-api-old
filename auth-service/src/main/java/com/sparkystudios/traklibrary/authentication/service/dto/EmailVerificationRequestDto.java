package com.sparkystudios.traklibrary.authentication.service.dto;

import lombok.Data;

@Data
public class EmailVerificationRequestDto {

    private String emailAddress;

    private String verificationCode;
}

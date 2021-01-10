package com.sparkystudios.traklibrary.authentication.service.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class EmailVerificationRequestDto {

    @JsonProperty("email_address")
    private String emailAddress;

    @JsonProperty("verification_code")
    private String verificationCode;
}

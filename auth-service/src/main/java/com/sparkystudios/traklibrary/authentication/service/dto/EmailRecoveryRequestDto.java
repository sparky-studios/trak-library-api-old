package com.sparkystudios.traklibrary.authentication.service.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class EmailRecoveryRequestDto {

    @JsonProperty("email_address")
    private String emailAddress;

    @JsonProperty("recovery_token")
    private String recoveryToken;
}

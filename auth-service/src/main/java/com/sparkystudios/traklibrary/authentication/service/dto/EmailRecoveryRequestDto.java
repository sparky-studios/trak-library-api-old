package com.sparkystudios.traklibrary.authentication.service.dto;

import lombok.Data;

@Data
public class EmailRecoveryRequestDto {

    private String emailAddress;

    private String recoveryToken;
}

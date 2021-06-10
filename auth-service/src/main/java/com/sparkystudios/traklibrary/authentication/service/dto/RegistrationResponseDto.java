package com.sparkystudios.traklibrary.authentication.service.dto;

import lombok.Data;

@Data
public class RegistrationResponseDto {

    private long userId;

    private byte[] qrData;
}

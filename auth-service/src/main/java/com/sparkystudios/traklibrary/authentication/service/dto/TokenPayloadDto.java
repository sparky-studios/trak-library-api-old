package com.sparkystudios.traklibrary.authentication.service.dto;

import lombok.Data;

@Data
public class TokenPayloadDto {

    private String accessToken;

    private String refreshToken;
}

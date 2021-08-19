package com.sparkystudios.traklibrary.authentication.service.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class TokenPayloadDto {

    private String tokenType;

    private LocalDateTime issuedAt;

    private LocalDateTime expiresAt;

    private String accessToken;

    private String refreshToken;

    private String scope;
}

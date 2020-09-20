package com.sparkystudios.traklibrary.security.dto;

import lombok.Data;

@Data
public class AuthenticatedUserDto {

    private long userId;

    private boolean verified;

    private String token;
}

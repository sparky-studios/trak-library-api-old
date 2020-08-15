package com.traklibrary.game.service.dto;

import lombok.Data;

@Data
public class AuthenticatedUserDto {

    private long userId;

    private String token;
}

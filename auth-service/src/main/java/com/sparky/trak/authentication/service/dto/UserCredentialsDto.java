package com.sparky.trak.authentication.service.dto;

import lombok.Data;

/**
 * Simple POJO that represents the request body that is passed to an /auth request. The only authorization
 * needed to return a JSON Web Token is the username and the password of the associated user.
 *
 * @author Sparky Studios
 */
@Data
public class UserCredentialsDto {

    private String username;

    private String password;
}

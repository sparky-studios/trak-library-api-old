package com.sparky.trak.authentication.service.dto;

import com.sparky.trak.authentication.domain.User;
import lombok.Data;

/**
 * Simple POJO DTO that is used to represent the request body that is passed to an authorization request to retrieve the
 * JWT for a given {@link User}. The only information needed to return a valid JWT for the given user information is the username
 * and the password of the associated user. Unlike other DTO's within the API, no validation is done on this DTO, the reason for this
 * is the fact that if credentials are incorrect, authentication will not occur and no JWT will be returned.
 *
 * @since 1.0.0
 * @author Sparky Studios
 */
@Data
public class UserCredentialsDto {

    private String username;

    private String password;
}

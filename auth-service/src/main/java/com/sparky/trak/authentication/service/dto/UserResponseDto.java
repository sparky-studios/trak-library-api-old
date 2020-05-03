package com.sparky.trak.authentication.service.dto;

import com.sparky.trak.authentication.domain.User;
import lombok.Data;

/**
 * The {@link UserResponseDto} is a simple POJO DTO that is used during login to provide the client with the
 * minimum amount of information needed for smooth interaction with different services. It allows for storage
 * of the user ID, which is used in other services to relate {@link User}'s to their individual tracked information
 * and their verification status, which can be used to ensure that verification occurs before normal user
 * interaction can occur.
 *
 * @since 1.0.0
 * @author Sparky Studios
 */
@Data
public class UserResponseDto {

    private long id;

    private String username;

    private boolean verified;
}

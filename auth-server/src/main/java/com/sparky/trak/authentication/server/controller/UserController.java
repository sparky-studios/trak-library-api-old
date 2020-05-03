package com.sparky.trak.authentication.server.controller;

import com.sparky.trak.authentication.server.annotation.AllowedForUser;
import com.sparky.trak.authentication.server.exception.ApiError;
import com.sparky.trak.authentication.service.UserService;
import com.sparky.trak.authentication.service.dto.CheckedResponse;
import com.sparky.trak.authentication.service.dto.RegistrationRequestDto;
import com.sparky.trak.authentication.service.dto.UserResponseDto;
import com.sparky.trak.authentication.service.dto.UserDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * The {@link UserController} is a controller class that exposes a small number of specialized end-points focused around the
 * creation, verification and authentication of Trak users. Most of the end-points within this controller are protected by
 * authentication annotations and additional JWT security, the only end-point anonymously available /auth/users, which is used
 * to create new users. It should be noted that the controller itself contains very little logic, the logic is contained within the
 * {@link UserService}.
 *
 * @since 1.0.0
 * @author Sparky Studios
 */
@RequiredArgsConstructor
@RestController
@RequestMapping(value = "/auth", produces = MediaType.APPLICATION_JSON_VALUE)
public class UserController {

    private final UserService userService;

    /**
     * End-point that will create a new {@link UserDto} and persist the information provided into the underlying
     * database. When a new {@link UserDto} entity is created, it will first check that details provided do not clash
     * with an existing user within the database, this is done to prevent duplicate usernames from being used. If the
     * details provided are unique, the {@link UserDto} will be flagged as not verified by default and will only have
     * base {@link com.sparky.trak.authentication.domain.UserRole} privileges. Success will return a 201 status code.
     *
     * If this end-point succeeds, the user will still not be able to use their account until it has been verified using
     * the verification code emailed to the address they provided. It should be noted that this endpoint can also not
     * be called with invalid credentials, the username and email address provided must be valid and the password
     * must reach the minimum requirements provided within {@link com.sparky.trak.authentication.service.validation.ValidPassword}.
     *
     * If the end-point fails to create a {@link UserDto}, the endpoint will return an {@link ApiError}
     * with additional details and exception messages, otherwise it'll return a {@link UserResponseDto} which
     * contains their user ID and username.
     *
     * @param registrationRequestDto The registration information to try and create the {@link UserDto} with.
     *
     * @return A {@link UserResponseDto} instance which represents some bare-bone details about the user.
     */
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/users")
    public CheckedResponse<UserResponseDto> save(@Validated @RequestBody RegistrationRequestDto registrationRequestDto) {
        return userService.save(registrationRequestDto);
    }

    /**
     * End-point that will retrieve some basic information about a given {@link UserDto}. The information provided
     * won't contain any personally identifying information, instead opting only to return the ID, username and the
     * current verification state of the user.
     *
     * It should be noted, that the information will only be returned to either the {@link UserDto} that matches
     * the given username, or a {@link UserDto} with elevated privileges.
     *
     * @return The user that matches the given username, as a {@link UserResponseDto}.
     */
    @AllowedForUser
    @GetMapping("/users/{username}")
    public UserResponseDto findByUsername(@PathVariable String username) {
        return userService.findByUsername(username);
    }

    /**
     * End-point that is used to verify the {@link UserDto} associated with the given username. Verification is successful
     * when the username provided points to a valid entry and the verification code provided matches the one assigned to
     * the account.
     *
     * If the verification code provided is incorrect, an exception will be thrown and a {@link ApiError} will be returned
     * to the client. If the {@link UserDto} that the username matches to is already verified, extra verification of the
     * account does not take place. It should be noted, similar to the /verified endpoint, the user can only verify the
     * account that matches their username, they cannot verify other accounts unless they have elevated privileges.
     *
     * @param username The username of the {@link UserDto} to verify.
     * @param verificationCode The verification code to check verification against.
     */
    @AllowedForUser
    @PutMapping("/users/{username}/verify")
    public void verify(@PathVariable String username, @RequestParam("verification-code") short verificationCode) {
        userService.verify(username, verificationCode);
    }
}

package com.sparky.trak.authentication.server.controller;

import com.sparky.trak.authentication.server.annotation.AllowedForUser;
import com.sparky.trak.authentication.server.exception.ApiError;
import com.sparky.trak.authentication.server.response.RestResponse;
import com.sparky.trak.authentication.service.UserService;
import com.sparky.trak.authentication.service.dto.RegistrationRequestDto;
import com.sparky.trak.authentication.service.dto.UserDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping(value = "/auth")
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
     * with additional details and exception messages.
     *
     * @param registrationRequestDto The registration information to try and create the {@link UserDto} with.
     */
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/users")
    public void save(@Validated @RequestBody RegistrationRequestDto registrationRequestDto) {
        userService.save(registrationRequestDto);
    }

    /**
     * End-point that will check what the verification state is of the provided user. If the username provided
     * does not match any within the system, an exception will be thrown and a {@link ApiError} with a bad request
     * status code will be returned.
     *
     * It should be noted, that the verification state can only be checked for the same user that is calling this
     * end-point, a user with a user role privilege cannot check the state of other users.
     *
     * @param username The username of the {@link UserDto} to check the verification state for.
     *
     * @return The verification state of the associated {@link UserDto}.
     */
    @AllowedForUser
    @GetMapping("/users/{username}/verified")
    public RestResponse<Boolean> isVerified(@PathVariable String username) {
        return new RestResponse<>(userService.isVerified(username));
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

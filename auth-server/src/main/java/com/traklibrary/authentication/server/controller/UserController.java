package com.traklibrary.authentication.server.controller;

import com.traklibrary.authentication.server.annotation.AllowedForUser;
import com.traklibrary.authentication.server.exception.ApiError;
import com.traklibrary.authentication.service.UserService;
import com.traklibrary.authentication.domain.UserRole;
import com.traklibrary.authentication.service.dto.*;
import com.traklibrary.authentication.service.validation.ValidPassword;
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
@RequestMapping(value = "/users", consumes = MediaType.APPLICATION_JSON_VALUE, produces = "application/vnd.traklibrary.v1.0+json")
public class UserController {

    private final UserService userService;

    /**
     * End-point that will create a new {@link UserDto} and persist the information provided into the underlying
     * database. When a new {@link UserDto} entity is created, it will first check that details provided do not clash
     * with an existing user within the database, this is done to prevent duplicate usernames from being used. If the
     * details provided are unique, the {@link UserDto} will be flagged as not verified by default and will only have
     * base {@link UserRole} privileges. Success will return a 201 status code.
     *
     * If this end-point succeeds, the user will still not be able to use their account until it has been verified using
     * the verification code emailed to the address they provided. It should be noted that this endpoint can also not
     * be called with invalid credentials, the username and email address provided must be valid and the password
     * must reach the minimum requirements provided within {@link ValidPassword}.
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
    @PostMapping
    public CheckedResponse<UserResponseDto> save(@Validated @RequestBody RegistrationRequestDto registrationRequestDto) {
        return userService.save(registrationRequestDto);
    }

    /**
     * End-point that is used when needing to recovery an existing {@link UserDto}. This method will check that the
     * recovery information provided is in a valid state, before passing the information off to the {@link UserService#update(RecoveryRequestDto)}
     * method. The method will return a successful response if the {@link RecoveryRequestDto} information
     * provided points to a valid account and contains the correct recovery token, if successful user information will
     * be returned a {@link UserResponseDto}.
     *
     * If the method fails to update an existing {@link UserDto} or contains invalid recovery information, a
     * {@link ApiError} will be returned containing the error details.
     *
     * @param recoveryRequestDto The recovery information to try and update a {@link UserDto} with.
     *
     * @return A {@link UserResponseDto} instance which represents some bare-bones information about the {@link UserDto}.
     */
    @PutMapping
    public CheckedResponse<UserResponseDto> update(@Validated @RequestBody RecoveryRequestDto recoveryRequestDto) {
        return userService.update(recoveryRequestDto);
    }

    /**
     * End-point that is used to verify the {@link UserDto} associated with the given username. Verification is successful
     * when the username provided points to a valid entry and the verification code provided matches the one assigned to
     * the account.
     *
     * If the verification code provided is incorrect, the {@link CheckedResponse} will contain <code>false</code> and
     * an error message stating to the user that the value is incorrect. If the {@link UserDto} that the username matches to
     * is already verified, extra verification of the account does not take place. It should be noted, similar to the /verified
     * endpoint, the user can only verify the account that matches their username, they cannot verify other accounts unless
     * they have elevated privileges.
     *
     * @param username The username of the {@link UserDto} to verify.
     * @param verificationCode The verification code to check verification against.
     *
     * @return A {@link CheckedResponse} specifying the current verification state of the user.
     */
    @AllowedForUser
    @PutMapping("/{username}/verify")
    public CheckedResponse<Boolean> verify(@PathVariable String username, @RequestParam("verification-code") String verificationCode) {
        return userService.verify(username, verificationCode);
    }

    /**
     * End-point that is used to re-verify an existing {@link UserDto} that is associated with the given username. Re-verification
     * will remove any verified information associated with the user and re-generate and re-populate the verification code and the
     * expiry date for the code.
     *
     * Similar to other end-points, the user can only re-verify accounts that they have the correct authentication for, they cannot
     * re-verify accounts they are not associated with unless they have elevated privileges.
     *
     * @param username The username of the {@link UserDto} to re-verify.
     */
    @AllowedForUser
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PutMapping("/{username}/reverify")
    public void reVerify(@PathVariable String username) {
        userService.reverify(username);
    }

    /**
     * End-point that is used when a user has forgotten password and requests for their account to be recovered. Recovery involves
     * generating a recovery token for the given user and emailing their token to the specified email address, if it is registered
     * within the system. The recovery token is assigned to the user that matches the email address and is assigned for 24 hours to
     * aid in recovery. If the token has not been used within this time, it is removed and the user will have to request an additional
     * token.
     *
     * As no authentication is available at this time, this URL can be accessed anonymously. However, it cannot be used in abuse
     * to send email addresses to random accounts, the email address must be registered within the system to receive an email.
     *
     * @param emailAddress The email address of the {@link UserDto} to generate a recovery token for.
     */
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PutMapping("/recover")
    public void requestRecovery(@RequestParam("email-address") String emailAddress) {
        userService.requestRecovery(emailAddress);
    }
}

package com.sparkystudios.traklibrary.authentication.server.controller;

import com.sparkystudios.traklibrary.authentication.domain.User;
import com.sparkystudios.traklibrary.authentication.domain.UserRole;
import com.sparkystudios.traklibrary.authentication.server.exception.ApiError;
import com.sparkystudios.traklibrary.authentication.service.UserService;
import com.sparkystudios.traklibrary.authentication.service.dto.*;
import com.sparkystudios.traklibrary.authentication.service.validation.ValidPassword;
import com.sparkystudios.traklibrary.security.annotation.AllowedForUser;
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
 * @since 0.1.0
 * @author Sparky Studios
 */
@RequiredArgsConstructor
@RestController
@RequestMapping(value = "/users", produces = "application/vnd.traklibrary.v1+json")
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
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
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
    @PutMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public CheckedResponse<UserResponseDto> update(@Validated @RequestBody RecoveryRequestDto recoveryRequestDto) {
        return userService.update(recoveryRequestDto);
    }

    /**
     * End-point that is used to delete the {@link UserDto} that is mapped to the given username. When a {@link UserDto} chooses
     * to delete their account, it will first check to ensure the account they're deleting exists and their authentication matches
     * the {@link UserDto} they're trying to delete, before deleting all of the {@link UserDto}'s roles and then the {@link UserDto}
     * itself.
     *
     * Similar to other end-points, the user can only delete accounts that they have the correct authentication for, they cannot
     * delete accounts they are not associated with unless they have elevated privileges.
     *
     * @param username The username of the {@link UserDto} to delete.
     */
    @AllowedForUser
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{username}")
    public void deleteByUsername(@PathVariable String username) {
        userService.deleteByUsername(username);
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
    @PutMapping(value = "/{username}/verify", consumes = MediaType.APPLICATION_JSON_VALUE)
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
    @PutMapping(value = "/{username}/reverify", consumes = MediaType.APPLICATION_JSON_VALUE)
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
    @PutMapping(value = "/recover", consumes = MediaType.APPLICATION_JSON_VALUE)
    public void requestRecovery(@RequestParam("email-address") String emailAddress) {
        userService.requestRecovery(emailAddress);
    }

    /**
     * End-point that is used when a user first requests that they wish to change the password of their account. This end-point will not
     * change their password, instead this will send an email to the email address associated with their account containing a recovery token
     * that will have to be entered alongside the new password they want when requesting for it to be changed.
     *
     * Similar to other end-points, the user can only request a change password email if they have the correct authentication for the account
     * they're requesting the email for, they cannot request for an account they are not associated with unless they have elevated privileges.
     *
     * @param username The name of the username to send the change password email to.
     */
    @AllowedForUser
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PutMapping(value = "/{username}/request-change-password", consumes = MediaType.APPLICATION_JSON_VALUE)
    public void requestChangePassword(@PathVariable String username) {
        userService.requestChangePassword(username);
    }

    /**
     * End-point that is used when a user wants to change their password. When the user wishes to change their password, they must send
     * their current username and their recovery token, which should have been emailed to their account when the {@link UserController#requestChangePassword(String)}
     * was invoked. Without this second layer of authentication, the user cannot their their password.
     *
     * Similar to other end-points, the user can only request a change password email if they have the correct authentication for the account
     * they're requesting the email for, they cannot request for an account they are not associated with unless they have elevated privileges.
     *
     * @param username The username of the {@link User} to change the password for.
     * @param changePasswordRequestDto The DTO that contains the user's new requested password and the emailed recovery token.
     *
     * @return A {@link CheckedResponse} specifying if the password change was successful.
     */
    @AllowedForUser
    @PutMapping(value = "{username}/change-password", consumes = MediaType.APPLICATION_JSON_VALUE)
    public CheckedResponse<Boolean> changePassword(@PathVariable String username, @Validated @RequestBody ChangePasswordRequestDto changePasswordRequestDto) {
        return userService.changePassword(username, changePasswordRequestDto);
    }

    /**
     * End-point that is used when a {@link User} wants to their change their email address.
     * When a {@link User} chooses to change their email address, it will first check to ensure
     * the account they're selecting exists, before changing the email address and dispatching a new verification email so the user
     * can re-verify their account.
     *
     * Similar to other end-points, the user can only request a change password email if they have the correct authentication for the account
     * they're requesting the email for, they cannot request for an account they are not associated with unless they have elevated privileges.
     *
     * @param username The username of the {@link User} to change the email address for.
     * @param changeEmailAddressRequestDto The DTO that contains the user's new requested email address.
     *
     * @return A {@link CheckedResponse} specifying if the email change was successful.
     */
    @AllowedForUser
    @PutMapping(value = "/{username}/change-email-address", consumes = MediaType.APPLICATION_JSON_VALUE)
    public CheckedResponse<Boolean> changeEmailAddress(@PathVariable String username, @Validated @RequestBody ChangeEmailAddressRequestDto changeEmailAddressRequestDto) {
        return userService.changeEmailAddress(username, changeEmailAddressRequestDto.getEmailAddress());
    }
}

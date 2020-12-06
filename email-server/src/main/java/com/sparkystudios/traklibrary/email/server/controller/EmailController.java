package com.sparkystudios.traklibrary.email.server.controller;

import com.sparkystudios.traklibrary.email.service.EmailService;
import com.sparkystudios.traklibrary.email.service.dto.EmailRecoveryRequestDto;
import com.sparkystudios.traklibrary.email.service.dto.EmailVerificationRequestDto;
import com.sparkystudios.traklibrary.email.service.exception.EmailFailedException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * The {@link EmailController} is a simple controller class that exposes a small number of end-points that are used to dispatch
 * different types of emails to users registered within the system. It should be noted that the controller itself
 * contains very little logic, the logic is contained within the {@link EmailService}, which specifies the behavior and which
 * email provider to utilize. It should be noted that to reach any of these end-points within this controller, the user
 * must have a valid JWT token and the email address that the email is being sent to must be within the system.
 *
 * @since 0.1.0
 * @author Sparky Studios
 */
@RequiredArgsConstructor
@RestController
@RequestMapping(value = "/", consumes = MediaType.APPLICATION_JSON_VALUE, produces = "application/vnd.traklibrary.v1+json")
public class EmailController {

    private final EmailService emailService;

    /**
     * End-point that will dispatch a verification email to the given email address. When the service method is invoked
     * by the end-point, it will verify that the email address matches the currently authenticated user, this is to
     * prevent the end-point from being used to send emails to random accounts when the user does not have permission
     * to do so.
     *
     * If the email is sent successfully, the end-point will return a 204 response code, otherwise if any issues
     * occurred a {@link EmailFailedException} will be thrown.
     *
     * @param emailVerificationRequestDto The body containing the email and verification code to dispatch.
     */
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PutMapping("/verification")
    public void sendVerificationEmail(@Validated @RequestBody EmailVerificationRequestDto emailVerificationRequestDto) {
        emailService.sendVerificationEmail(emailVerificationRequestDto);
    }

    /**
     * End-point that will dispatch an account recovery email to the given email address. When the service method is invoked
     * by the end-point, it will verify that the email address matches the currently authenticated user, this is to
     * prevent the end-point from being used to send emails to random accounts when the user does not have permission
     * to do so.
     *
     * If the email is sent successfully, the end-point will return a 204 response code, otherwise if any issues
     * occurred a {@link EmailFailedException} will be thrown.
     *
     * @param emailRecoveryRequestDto The body containing the email and recovery token to dispatch.
     */
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PutMapping("/recovery")
    public void sendRecoveryEmail(@Validated @RequestBody EmailRecoveryRequestDto emailRecoveryRequestDto) {
        emailService.sendRecoveryEmail(emailRecoveryRequestDto);
    }

    /**
     * End-point that will dispatch a change password email to the given email address. When the service method is invoked
     * by the end-point, it will verify that the email address matches the currently authenticated user, this is to
     * prevent the end-point from being used to send emails to random accounts when the user does not have permission
     * to do so.
     *
     * If the email is sent successfully, the end-point will return a 204 response code, otherwise if any issues
     * occurred a {@link EmailFailedException} will be thrown.
     *
     * @param emailRecoveryRequestDto The body containing the email and recovery token to dispatch.
     */
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PutMapping("/change-password")
    public void sendChangePasswordEmail(@Validated @RequestBody EmailRecoveryRequestDto emailRecoveryRequestDto) {
        emailService.sendChangePasswordEmail(emailRecoveryRequestDto);
    }
}

package com.sparky.trak.email.server.controller;

import com.sparky.trak.email.server.annotation.AllowedForUser;
import com.sparky.trak.email.service.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

/**
 * The {@link EmailController} is a simple controller class that exposes a small number of end-points that are used to dispatch
 * different types of emails to users registered within the system. It should be noted that the controller itself
 * contains very little logic, the logic is contained within the {@link EmailService}, which specifies the behavior and which
 * email provider to utilize. It should be noted that to reach any of these end-points within this controller, the user
 * must have a valid JWT token and the email address that the email is being sent to must be within the system.
 *
 * @since 1.0.0
 * @author Sparky Studios
 */
@RequiredArgsConstructor
@RestController
@RequestMapping(value = "/v1/emails")
public class EmailController {

    private final EmailService emailService;

    /**
     * End-point that will dispatch a verification email to the given email address. When the service method is invoked
     * by the end-point, it will verify that the email address matches the currently authenticated user, this is to
     * prevent the end-point from being used to send emails to random accounts when the user does not have permission
     * to do so.
     *
     * If the email is sent successfully, the end-point will return a 204 response code, otherwise if any issues
     * occurred a {@link com.sparky.trak.email.service.exception.EmailFailedException} will be thrown.
     *
     * @param emailAddress The email address to dispatch the verification email to.
     * @param verificationCode The verification code of the user to attach to the email.
     */
    @AllowedForUser
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PutMapping("/verification")
    public void sendVerificationEmail(@RequestParam("email-address") String emailAddress,
                                      @RequestParam("verification-code") short verificationCode) {
        emailService.sendVerificationEmail(emailAddress, verificationCode);
    }
}

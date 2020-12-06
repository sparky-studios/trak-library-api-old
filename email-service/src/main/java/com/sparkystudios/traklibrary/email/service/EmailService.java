package com.sparkystudios.traklibrary.email.service;

import com.sparkystudios.traklibrary.email.service.dto.EmailRecoveryRequestDto;
import com.sparkystudios.traklibrary.email.service.dto.EmailVerificationRequestDto;
import com.sparkystudios.traklibrary.email.service.exception.EmailFailedException;
import com.sparkystudios.traklibrary.email.service.impl.EmailServiceThymeleafImpl;

/**
 * The {@link EmailService} is an interface that is used to define all of the methods that
 * have to be implemented to ensure a contract between the Trak Library API and the underlying
 * email provider. Each different email that can be dispatched will be implemented within its
 * own method for clarity, instead of relying on enumerations or other methods of differentiation.
 *
 * For an implementation, refer to the {@link EmailServiceThymeleafImpl}
 * which utilizes thymeleaf for the email template and AWS Simple Email Service as the email provider.
 *
 * @since 0.1.0
 * @author Sparky Studios
 */
public interface EmailService {

    /**
     * Given an email address and verification code, this method will dispatch a verification email
     * to the specified address using the email provider defined within the {@link EmailService}
     * implementation. If any errors occur when dispatching an email, an {@link EmailFailedException}
     * will be thrown and the information will be returned to the API callee.
     *
     * It should be noted, that no validation needs to occur with any {@link EmailService}
     * implementation, validation of the fields should occur at the controller level.
     *
     * @param emailVerificationRequestDto The email and verification code to dispatch.
     */
    void sendVerificationEmail(EmailVerificationRequestDto emailVerificationRequestDto);

    /**
     * Given an email address and a password, this method will dispatch an account recovery email
     * to the specified address using the email provider defined within the {@link EmailService}
     * implementation. If any errors occur when dispatching an email, an {@link EmailFailedException}
     * will be thrown and the information will be returned to the API callee.
     *
     * It should be noted, that no validation needs to occur with any {@link EmailService}
     * implementation, validation of the fields should occur at the controller level.
     *
     * @param emailRecoveryRequestDto The email and recovery code to dispatch.
     */
    void sendRecoveryEmail(EmailRecoveryRequestDto emailRecoveryRequestDto);

    /**
     * Given an email address and a password, this method will dispatch a change password email
     * to the specified address using the email provider defined within the {@link EmailService}
     * implementation. If any errors occur when dispatching an email, an {@link EmailFailedException}
     * will be thrown and the information will be returned to the API callee.
     *
     * It should be noted, that no validation needs to occur with any {@link EmailService}
     * implementation, validation of the fields should occur at the controller level.
     *
     * @param emailRecoveryRequestDto The email and recovery code to dispatch.
     */
    void sendChangePasswordEmail(EmailRecoveryRequestDto emailRecoveryRequestDto);
}
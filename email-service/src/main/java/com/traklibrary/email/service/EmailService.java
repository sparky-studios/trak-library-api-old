package com.traklibrary.email.service;

import com.traklibrary.email.service.exception.EmailFailedException;
import com.traklibrary.email.service.impl.EmailServiceThymeleafImpl;

/**
 * The {@link EmailService} is an interface that is used to define all of the methods that
 * have to be implemented to ensure a contract between the Trak API and the underlying
 * email provider. Each different email that can be dispatched will be implemented within its
 * own method for clarity, instead of relying on enumerations or other methods of differentiation.
 *
 * For an implementation, refer to the {@link EmailServiceThymeleafImpl}
 * which utilizes thymeleaf for the email template and AWS Simple Email Service as the email provider.
 *
 * @since 1.0.0
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
     * @param emailAddress The email address to send the verification email to.
     * @param verificationCode The verification code to attach to the email.
     */
    void sendVerificationEmail(String emailAddress, String verificationCode);

    /**
     * Given an email address and a password, this method will dispatch an account recovery email
     * to the specified address using the email provider defined within the {@link EmailService}
     * implementation. If any errors occur when dispatching an email, an {@link EmailFailedException}
     * will be thrown and the information will be returned to the API callee.
     *
     * It should be noted, that no validation needs to occur with any {@link EmailService}
     * implementation, validation of the fields should occur at the controller level.
     *
     * @param emailAddress The email address to send the account recovery email to.
     * @param recoveryToken The randomly generated recovery token to attach to the email.
     */
    void sendRecoveryEmail(String emailAddress, String recoveryToken);
}
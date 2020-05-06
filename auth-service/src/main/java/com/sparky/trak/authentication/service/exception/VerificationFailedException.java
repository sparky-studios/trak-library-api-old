package com.sparky.trak.authentication.service.exception;

import com.sparky.trak.authentication.domain.User;

/**
 * The {@link VerificationFailedException} is a type of exception that is thrown during the
 * verification of a {@link User} when the information provided to the end-point is not valid or
 * does not match the verification code associated with the {@link User}.
 *
 * @since 1.0.0
 * @author Sparky Studios
 */
public class VerificationFailedException extends RuntimeException {

    public VerificationFailedException() {
        super();
    }

    public VerificationFailedException(String message) {
        super(message);
    }

    public VerificationFailedException(String message, Throwable cause) {
        super(message, cause);
    }

    public VerificationFailedException(Throwable cause) {
        super(cause);
    }
}

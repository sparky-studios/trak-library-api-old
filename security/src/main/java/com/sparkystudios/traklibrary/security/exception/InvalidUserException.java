package com.sparkystudios.traklibrary.security.exception;

/**
 * Exception that is used to represent when a authenticated user is trying to change the
 * data for an account different to theirs. This exception will only be thrown if the
 * user in question doesn't have elevated privileges.
 *
 * @since 0.1.0
 * @author Sparky Studios
 */
public class InvalidUserException extends RuntimeException {

    public InvalidUserException() {
        super();
    }

    public InvalidUserException(String message) {
        super(message);
    }

    public InvalidUserException(String message, Throwable cause) {
        super(message, cause);
    }

    public InvalidUserException(Throwable cause) {
        super(cause);
    }
}

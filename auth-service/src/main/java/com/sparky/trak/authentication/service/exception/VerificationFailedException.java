package com.sparky.trak.authentication.service.exception;

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

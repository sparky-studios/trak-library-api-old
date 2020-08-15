package com.traklibrary.email.service.exception;

public class EmailFailedException extends RuntimeException {

    public EmailFailedException() {
        super();
    }

    public EmailFailedException(String message) {
        super(message);
    }

    public EmailFailedException(String message, Throwable cause) {
        super(message, cause);
    }

    public EmailFailedException(Throwable cause) {
        super(cause);
    }
}

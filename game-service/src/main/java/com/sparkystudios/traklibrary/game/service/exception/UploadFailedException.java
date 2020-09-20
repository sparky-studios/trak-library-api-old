package com.sparkystudios.traklibrary.game.service.exception;

public class UploadFailedException extends RuntimeException {

    public UploadFailedException() {
        super();
    }

    public UploadFailedException(String message) {
        super(message);
    }

    public UploadFailedException(String message, Throwable cause) {
        super(message, cause);
    }

    public UploadFailedException(Throwable cause) {
        super(cause);
    }
}

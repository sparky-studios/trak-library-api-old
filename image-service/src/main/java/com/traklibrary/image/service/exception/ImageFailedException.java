package com.traklibrary.image.service.exception;

public class ImageFailedException extends RuntimeException {

    public ImageFailedException() {
        super();
    }

    public ImageFailedException(String message) {
        super(message);
    }

    public ImageFailedException(String message, Throwable cause) {
        super(message, cause);
    }

    public ImageFailedException(Throwable cause) {
        super(cause);
    }
}

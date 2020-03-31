package com.sparky.trak.game.server.exception;

import lombok.AccessLevel;
import lombok.Data;
import lombok.Setter;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;

@Data
public class ApiError {

    private HttpStatus status;
    @Setter(AccessLevel.NONE)
    private LocalDateTime timestamp;
    private String message;
    private String debugMessage;

    @Setter(AccessLevel.NONE)
    private Collection<ApiSubError> subErrors = new ArrayList<>();

    private ApiError() {
        timestamp = LocalDateTime.now();
    }

    public ApiError(HttpStatus status) {
        this();
        this.status = status;
    }
}
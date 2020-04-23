package com.sparky.trak.authentication.server.exception;

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
    private LocalDateTime time;
    private String message;
    private String debugMessage;

    @Setter(AccessLevel.NONE)
    private Collection<ApiSubError> subErrors = new ArrayList<>();

    private ApiError() {
        time = LocalDateTime.now();
    }

    public ApiError(HttpStatus status) {
        this();
        this.status = status;
    }
}
package com.sparkystudios.traklibrary.security.exception;

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
    private String error;

    @Setter(AccessLevel.NONE)
    private Collection<ApiSubError> details = new ArrayList<>();

    private ApiError() {
        time = LocalDateTime.now();
    }

    public ApiError(HttpStatus status) {
        this();
        this.status = status;
    }
}
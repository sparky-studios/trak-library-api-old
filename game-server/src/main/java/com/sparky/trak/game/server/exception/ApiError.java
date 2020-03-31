package com.sparky.trak.game.webapp.exception;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;
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
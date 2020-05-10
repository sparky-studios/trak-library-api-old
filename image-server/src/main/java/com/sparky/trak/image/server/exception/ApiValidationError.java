package com.sparky.trak.image.server.exception;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
class ApiValidationError extends ApiSubError {

    private String object;
    private String field;
    private Object rejectedValue;
    private String message;
}
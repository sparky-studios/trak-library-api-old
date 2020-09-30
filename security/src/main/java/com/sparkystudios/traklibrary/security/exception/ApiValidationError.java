package com.sparkystudios.traklibrary.security.exception;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public
class ApiValidationError implements ApiSubError {

    private String object;
    private String field;
    private Object rejectedValue;
    private String message;
}
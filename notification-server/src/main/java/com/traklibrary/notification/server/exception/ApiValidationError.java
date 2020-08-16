package com.traklibrary.notification.server.exception;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
class ApiValidationError implements ApiSubError {

    private String object;
    private String field;
    private Object rejectedValue;
    private String message;
}
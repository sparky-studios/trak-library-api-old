package com.sparkystudios.traklibrary.security.exception;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME)
@JsonSubTypes({
        @JsonSubTypes.Type(value = ApiValidationError.class, name = "ApiValidationError"),
})
public interface ApiSubError {
}

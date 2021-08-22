package com.sparkystudios.traklibrary.authentication.server.exception;

import com.sparkystudios.traklibrary.security.exception.ApiError;
import com.sparkystudios.traklibrary.security.exception.ApiValidationError;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import javax.persistence.EntityExistsException;
import javax.persistence.EntityNotFoundException;
import javax.validation.ConstraintViolationException;
import java.util.stream.Collectors;

@Slf4j
@Order(Ordered.HIGHEST_PRECEDENCE)
@ControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(EntityNotFoundException.class)
    protected ResponseEntity<Object> handleEntityNotFound(EntityNotFoundException ex) {
        log.error("Entity not found", ex);

        var apiError = new ApiError(HttpStatus.NOT_FOUND);
        apiError.setError(ex.getMessage());

        return new ResponseEntity<>(apiError, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(EntityExistsException.class)
    protected ResponseEntity<Object> handleEntityExists(EntityExistsException ex) {
        log.error("Entity exists", ex);

        var apiError = new ApiError(HttpStatus.BAD_REQUEST);
        apiError.setError(ex.getMessage());

        return new ResponseEntity<>(apiError, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(BadCredentialsException.class)
    protected ResponseEntity<Object> handlBadCredentials(BadCredentialsException ex) {
        log.error("Bad credentials", ex);

        var apiError = new ApiError(HttpStatus.BAD_REQUEST);
        apiError.setError(ex.getMessage());

        return new ResponseEntity<>(apiError, HttpStatus.BAD_REQUEST);
    }

    @Override
    @NonNull
    protected ResponseEntity<Object> handleHttpMessageNotReadable(@NonNull HttpMessageNotReadableException ex, @NonNull HttpHeaders headers, @NonNull HttpStatus status, @NonNull WebRequest request) {
        log.error("HTTP message not readable", ex);

        var apiError = new ApiError(HttpStatus.BAD_REQUEST);
        apiError.setError(ex.getMessage());

        return new ResponseEntity<>(apiError, headers, status);
    }

    @Override
    @NonNull
    protected ResponseEntity<Object> handleMethodArgumentNotValid(@NonNull MethodArgumentNotValidException ex, @NonNull HttpHeaders headers, @NonNull HttpStatus status, @NonNull WebRequest request) {
        log.error("Method argument not valid", ex);

        var apiError = new ApiError(HttpStatus.BAD_REQUEST);
        apiError.setError(ex.getMessage());

        // Stream through each sub-error and add all of them to the response.
        apiError.getDetails().addAll(ex.getBindingResult().getFieldErrors()
                .stream()
                .map(fe -> new ApiValidationError(fe.getObjectName(), fe.getField(), fe.getRejectedValue(), fe.getDefaultMessage()))
                .collect(Collectors.toList()));

        return new ResponseEntity<>(apiError, headers, status);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    protected ResponseEntity<Object> handleConstraintViolation(ConstraintViolationException ex) {
        log.error("Constraint violation", ex);

        var apiError = new ApiError(HttpStatus.BAD_REQUEST);
        apiError.setError(ex.getMessage());

        apiError.getDetails().addAll(ex.getConstraintViolations()
                .stream()
                .map(c -> new ApiValidationError(c.getRootBeanClass().getSimpleName(), c.getPropertyPath().toString(), c.getInvalidValue().toString(), c.getMessage()))
                .collect(Collectors.toList()));

        return ResponseEntity
                .badRequest()
                .contentType(MediaType.APPLICATION_JSON)
                .body(apiError);
    }
}

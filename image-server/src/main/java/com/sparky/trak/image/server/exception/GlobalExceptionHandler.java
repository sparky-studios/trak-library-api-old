package com.sparky.trak.image.server.exception;

import com.sparky.trak.image.service.exception.ImageFailedException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.stream.Collectors;

@Slf4j
@Order(Ordered.HIGHEST_PRECEDENCE)
@ControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(ImageFailedException.class)
    protected ResponseEntity<Object> handleImageFailed(ImageFailedException ex) {
        log.error("Image failure", ex);

        ApiError apiError = new ApiError(HttpStatus.BAD_REQUEST);
        apiError.setMessage(ex.getMessage());

        return new ResponseEntity<>(apiError, HttpStatus.BAD_REQUEST);
    }

    @Override
    protected ResponseEntity<Object> handleHttpMessageNotReadable(HttpMessageNotReadableException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        log.error("HTTP message not readable", ex);

        ApiError apiError = new ApiError(HttpStatus.BAD_REQUEST);
        apiError.setMessage(ex.getMessage());

        return new ResponseEntity<>(apiError, headers, status);
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        log.error("Method argument not valid", ex);

        ApiError apiError = new ApiError(HttpStatus.BAD_REQUEST);
        apiError.setMessage(ex.getMessage());

        // Stream through each sub-error and add all of them to the response.
        apiError.getSubErrors().addAll(ex.getBindingResult().getFieldErrors()
                .stream()
                .map(fe -> new ApiValidationError(fe.getObjectName(), fe.getField(), fe.getRejectedValue(), fe.getDefaultMessage()))
                .collect(Collectors.toList()));

        return new ResponseEntity<>(apiError, headers, status);
    }
}

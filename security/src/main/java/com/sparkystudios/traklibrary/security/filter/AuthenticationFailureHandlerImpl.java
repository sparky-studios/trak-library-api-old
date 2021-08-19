package com.sparkystudios.traklibrary.security.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sparkystudios.traklibrary.security.exception.ApiError;
import com.sparkystudios.traklibrary.security.exception.AuthenticationMethodNotSupportedException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Simple handler class that is invoked by the authentication process when an authentication request fails.
 *
 * @author Tucasi Ltd.
 */
@Component
@RequiredArgsConstructor
public class AuthenticationFailureHandlerImpl implements AuthenticationFailureHandler {

    private final ObjectMapper objectMapper;

    /**
     * Invoked by the authentication process when an authentication request fails. When the request fails
     * an {@link ApiError} instance will be created with some additional error information and written to the response body
     * of the request.
     *
     * @param httpServletRequest The {@link HttpServletRequest} instance of the request.
     * @param httpServletResponse The {@link HttpServletResponse} instance of the request.
     * @param e The exception that was thrown that failed authentication.
     *
     * @throws IOException Thrown if the {@link ApiError} instance cannot be written to the response.
     */
    @Override
    public void onAuthenticationFailure(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, AuthenticationException e) throws IOException {

        httpServletResponse.setStatus(HttpStatus.UNAUTHORIZED.value());
        httpServletResponse.setContentType(MediaType.APPLICATION_JSON_VALUE);

        var apiError = new ApiError(HttpStatus.UNAUTHORIZED);

        if (e instanceof BadCredentialsException) {
            apiError.setError("Invalid credentials.");
        } else if (e instanceof AuthenticationMethodNotSupportedException) {
            apiError.setError(e.getMessage());
        } else {
            apiError.setError("Authentication failed due to an internal error.");
        }

        objectMapper.writeValue(httpServletResponse.getWriter(), apiError);
    }
}

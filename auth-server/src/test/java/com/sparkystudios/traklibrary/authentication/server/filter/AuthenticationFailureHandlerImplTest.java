package com.sparkystudios.traklibrary.authentication.server.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sparkystudios.traklibrary.authentication.server.exception.AuthenticationMethodNotSupportedException;
import com.sparkystudios.traklibrary.security.exception.ApiError;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.MessageSource;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Locale;

@ExtendWith(MockitoExtension.class)
class AuthenticationFailureHandlerImplTest {

    @Mock
    private MessageSource messageSource;

    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private AuthenticationFailureHandlerImpl authenticationFailureHandler;

    @Test
    public void onAuthenticationFailure_withBadCredentialsException_setsCorrectMessage() throws IOException {
        // Arrange
        AuthenticationException authenticationException = new BadCredentialsException("");

        Mockito.when(messageSource.getMessage(ArgumentMatchers.eq("authentication.exception.bad-credentials"), ArgumentMatchers.any(Object[].class), ArgumentMatchers.any(Locale.class)))
                .thenReturn("");

        HttpServletResponse httpServletResponse = Mockito.mock(HttpServletResponse.class);
        Mockito.when(httpServletResponse.getWriter())
                .thenReturn(Mockito.mock(PrintWriter.class));

        // Act
        authenticationFailureHandler.onAuthenticationFailure(null, httpServletResponse, authenticationException);

        // Assert
        Mockito.verify(messageSource, Mockito.atMostOnce())
                .getMessage(ArgumentMatchers.eq("authentication.exception.bad-credentials"), ArgumentMatchers.any(Object[].class), ArgumentMatchers.any(Locale.class));

        Mockito.verify(objectMapper, Mockito.atMostOnce())
                .writeValue(ArgumentMatchers.any(PrintWriter.class), ArgumentMatchers.any(ApiError.class));
    }

    @Test
    public void onAuthenticationFailure_withAuthenticationMethodNotSupportedException_setsCorrectMessage() throws IOException {
        // Arrange
        AuthenticationException authenticationException = Mockito.mock(AuthenticationMethodNotSupportedException.class);

        HttpServletResponse httpServletResponse = Mockito.mock(HttpServletResponse.class);
        Mockito.when(httpServletResponse.getWriter())
                .thenReturn(Mockito.mock(PrintWriter.class));

        // Act
        authenticationFailureHandler.onAuthenticationFailure(null, httpServletResponse, authenticationException);

        // Assert
        Mockito.verify(authenticationException, Mockito.atMostOnce())
                .getMessage();

        Mockito.verify(objectMapper, Mockito.atMostOnce())
                .writeValue(ArgumentMatchers.any(PrintWriter.class), ArgumentMatchers.any(ApiError.class));
    }

    @Test
    public void onAuthenticationFailure_withOtherException_setsCorrectMessage() throws IOException {
        // Arrange
        AuthenticationException authenticationException = new AuthenticationServiceException("");

        Mockito.when(messageSource.getMessage(ArgumentMatchers.eq("authentication.exception.authentication-failed"), ArgumentMatchers.any(Object[].class), ArgumentMatchers.any(Locale.class)))
                .thenReturn("");

        HttpServletResponse httpServletResponse = Mockito.mock(HttpServletResponse.class);
        Mockito.when(httpServletResponse.getWriter())
                .thenReturn(Mockito.mock(PrintWriter.class));

        // Act
        authenticationFailureHandler.onAuthenticationFailure(null, httpServletResponse, authenticationException);

        // Assert
        Mockito.verify(messageSource, Mockito.atMostOnce())
                .getMessage(ArgumentMatchers.eq("authentication.exception.authentication-failed"), ArgumentMatchers.any(Object[].class), ArgumentMatchers.any(Locale.class));

        Mockito.verify(objectMapper, Mockito.atMostOnce())
                .writeValue(ArgumentMatchers.any(PrintWriter.class), ArgumentMatchers.any(ApiError.class));
    }
}

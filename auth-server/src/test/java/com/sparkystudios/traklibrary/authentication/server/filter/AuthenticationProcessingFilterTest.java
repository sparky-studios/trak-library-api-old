package com.sparkystudios.traklibrary.authentication.server.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sparkystudios.traklibrary.security.exception.AuthenticationMethodNotSupportedException;
import com.sparkystudios.traklibrary.authentication.service.dto.LoginRequestDto;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.MessageSource;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.core.Authentication;

import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.Locale;

@ExtendWith(MockitoExtension.class)
class AuthenticationProcessingFilterTest {

    @Mock
    private MessageSource messageSource;

    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private AuthenticationProcessingFilter authenticationProcessingFilter;

    @Test
    public void attemptAuthentication_withNonPostRequest_throwsAuthenticationMethodNotSupportedException() {
        // Arrange
        HttpServletRequest httpServletRequest = Mockito.mock(HttpServletRequest.class);
        Mockito.when(httpServletRequest.getMethod())
                .thenReturn("PUT");

        Mockito.when(messageSource.getMessage(ArgumentMatchers.eq("authentication.exception.method-not-supported"), ArgumentMatchers.any(Object[].class), ArgumentMatchers.any(Locale.class)))
                .thenReturn("");

        // Assert
        Assertions.assertThatExceptionOfType(AuthenticationMethodNotSupportedException.class)
                .isThrownBy(() -> authenticationProcessingFilter.attemptAuthentication(httpServletRequest, null));
    }

    @Test
    public void attemptAuthentication_withNullUsername_throwsAuthenticationServiceException() throws IOException {
        // Arrange
        HttpServletRequest httpServletRequest = Mockito.mock(HttpServletRequest.class);
        Mockito.when(httpServletRequest.getMethod())
                .thenReturn("POST");

        Mockito.when(httpServletRequest.getReader())
                .thenReturn(Mockito.mock(BufferedReader.class));

        LoginRequestDto loginRequestDto = new LoginRequestDto();
        loginRequestDto.setUsername(null);
        loginRequestDto.setPassword("password");

        Mockito.when(objectMapper.readValue(ArgumentMatchers.any(Reader.class), ArgumentMatchers.eq(LoginRequestDto.class)))
                .thenReturn(loginRequestDto);

        Mockito.when(messageSource.getMessage(ArgumentMatchers.eq("authentication.exception.missing-credentials"), ArgumentMatchers.any(Object[].class), ArgumentMatchers.any(Locale.class)))
                .thenReturn("");

        // Assert
        Assertions.assertThatExceptionOfType(AuthenticationServiceException.class)
                .isThrownBy(() -> authenticationProcessingFilter.attemptAuthentication(httpServletRequest, null));
    }

    @Test
    public void attemptAuthentication_withEmptyUsername_throwsAuthenticationServiceException() throws IOException {
        // Arrange
        HttpServletRequest httpServletRequest = Mockito.mock(HttpServletRequest.class);
        Mockito.when(httpServletRequest.getMethod())
                .thenReturn("POST");

        Mockito.when(httpServletRequest.getReader())
                .thenReturn(Mockito.mock(BufferedReader.class));

        LoginRequestDto loginRequestDto = new LoginRequestDto();
        loginRequestDto.setUsername("     ");
        loginRequestDto.setPassword("password");

        Mockito.when(objectMapper.readValue(ArgumentMatchers.any(Reader.class), ArgumentMatchers.eq(LoginRequestDto.class)))
                .thenReturn(loginRequestDto);

        Mockito.when(messageSource.getMessage(ArgumentMatchers.eq("authentication.exception.missing-credentials"), ArgumentMatchers.any(Object[].class), ArgumentMatchers.any(Locale.class)))
                .thenReturn("");

        // Assert
        Assertions.assertThatExceptionOfType(AuthenticationServiceException.class)
                .isThrownBy(() -> authenticationProcessingFilter.attemptAuthentication(httpServletRequest, null));
    }

    @Test
    public void attemptAuthentication_withNullPassword_throwsAuthenticationServiceException() throws IOException {
        // Arrange
        HttpServletRequest httpServletRequest = Mockito.mock(HttpServletRequest.class);
        Mockito.when(httpServletRequest.getMethod())
                .thenReturn("POST");

        Mockito.when(httpServletRequest.getReader())
                .thenReturn(Mockito.mock(BufferedReader.class));

        LoginRequestDto loginRequestDto = new LoginRequestDto();
        loginRequestDto.setUsername("username");
        loginRequestDto.setPassword(null);

        Mockito.when(objectMapper.readValue(ArgumentMatchers.any(Reader.class), ArgumentMatchers.eq(LoginRequestDto.class)))
                .thenReturn(loginRequestDto);

        Mockito.when(messageSource.getMessage(ArgumentMatchers.eq("authentication.exception.missing-credentials"), ArgumentMatchers.any(Object[].class), ArgumentMatchers.any(Locale.class)))
                .thenReturn("");

        // Assert
        Assertions.assertThatExceptionOfType(AuthenticationServiceException.class)
                .isThrownBy(() -> authenticationProcessingFilter.attemptAuthentication(httpServletRequest, null));
    }

    @Test
    public void attemptAuthentication_withEmptyPassword_throwsAuthenticationServiceException() throws IOException {
        // Arrange
        HttpServletRequest httpServletRequest = Mockito.mock(HttpServletRequest.class);
        Mockito.when(httpServletRequest.getMethod())
                .thenReturn("POST");

        Mockito.when(httpServletRequest.getReader())
                .thenReturn(Mockito.mock(BufferedReader.class));

        LoginRequestDto loginRequestDto = new LoginRequestDto();
        loginRequestDto.setUsername("username");
        loginRequestDto.setPassword("     ");

        Mockito.when(objectMapper.readValue(ArgumentMatchers.any(Reader.class), ArgumentMatchers.eq(LoginRequestDto.class)))
                .thenReturn(loginRequestDto);

        Mockito.when(messageSource.getMessage(ArgumentMatchers.eq("authentication.exception.missing-credentials"), ArgumentMatchers.any(Object[].class), ArgumentMatchers.any(Locale.class)))
                .thenReturn("");

        // Assert
        Assertions.assertThatExceptionOfType(AuthenticationServiceException.class)
                .isThrownBy(() -> authenticationProcessingFilter.attemptAuthentication(httpServletRequest, null));
    }

    @Test
    public void attemptAuthentication_withValidCredentials_callsAuthenticate() throws IOException {
        // Arrange
        HttpServletRequest httpServletRequest = Mockito.mock(HttpServletRequest.class);
        Mockito.when(httpServletRequest.getMethod())
                .thenReturn("POST");

        Mockito.when(httpServletRequest.getReader())
                .thenReturn(Mockito.mock(BufferedReader.class));

        LoginRequestDto loginRequestDto = new LoginRequestDto();
        loginRequestDto.setUsername("username");
        loginRequestDto.setPassword("password");

        Mockito.when(objectMapper.readValue(ArgumentMatchers.any(Reader.class), ArgumentMatchers.eq(LoginRequestDto.class)))
                .thenReturn(loginRequestDto);

        AuthenticationManager authenticationManager = Mockito.mock(AuthenticationManager.class);
        Mockito.when(authenticationManager.authenticate(ArgumentMatchers.any()))
                .thenReturn(Mockito.mock(Authentication.class));

        authenticationProcessingFilter.setAuthenticationManager(authenticationManager);

        // Act
        authenticationProcessingFilter.attemptAuthentication(httpServletRequest, null);

        // Assert
        Mockito.verify(authenticationManager, Mockito.atMostOnce())
                .authenticate(ArgumentMatchers.any());
    }
}

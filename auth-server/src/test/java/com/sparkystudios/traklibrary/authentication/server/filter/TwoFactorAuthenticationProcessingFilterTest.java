package com.sparkystudios.traklibrary.authentication.server.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sparkystudios.traklibrary.authentication.service.dto.TwoFactorAuthenticationRequestDto;
import com.sparkystudios.traklibrary.security.exception.AuthenticationMethodNotSupportedException;
import com.sparkystudios.traklibrary.security.filter.JwtHeaderExtractor;
import com.sparkystudios.traklibrary.security.token.authentication.TwoFactorAuthenticationToken;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.core.Authentication;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.Locale;
import java.util.stream.Stream;

@ExtendWith(MockitoExtension.class)
class TwoFactorAuthenticationProcessingFilterTest {

    @Mock
    private MessageSource messageSource;

    @Mock
    private ObjectMapper objectMapper;

    @Mock
    private JwtHeaderExtractor jwtHeaderExtractor;

    @InjectMocks
    private TwoFactorAuthenticationProcessingFilter twoFactorAuthenticationProcessingFilter;

    private static Stream<Arguments> twoFactorCredentials() {
        return Stream.of(
                Arguments.of(null, "code"),
                Arguments.of("", "code"),
                Arguments.of("token", null),
                Arguments.of("token", "")
        );
    }

    @Test
    void attemptAuthentication_withNonPostRequest_throwsAuthenticationMethodNotSupportedException() {
        // Arrange
        HttpServletRequest httpServletRequest = Mockito.mock(HttpServletRequest.class);
        Mockito.when(httpServletRequest.getMethod())
                .thenReturn("PUT");

        Mockito.when(messageSource.getMessage(ArgumentMatchers.eq("authentication.exception.method-not-supported"), ArgumentMatchers.any(Object[].class), ArgumentMatchers.any(Locale.class)))
                .thenReturn("");

        // Assert
        Assertions.assertThatExceptionOfType(AuthenticationMethodNotSupportedException.class)
                .isThrownBy(() -> twoFactorAuthenticationProcessingFilter.attemptAuthentication(httpServletRequest, null));
    }

    @ParameterizedTest
    @MethodSource("twoFactorCredentials")
    void attemptAuthentication_withInvalidArguments_throwsAuthenticationServiceException(String jwt, String code) throws IOException {
        // Arrange
        HttpServletRequest httpServletRequest = Mockito.mock(HttpServletRequest.class);
        Mockito.when(httpServletRequest.getMethod())
                .thenReturn("POST");

        Mockito.when(httpServletRequest.getReader())
                .thenReturn(Mockito.mock(BufferedReader.class));

        Mockito.when(jwtHeaderExtractor.extract(ArgumentMatchers.isNull()))
                .thenReturn(jwt);

        var twoFactorAuthenticationRequestDto = new TwoFactorAuthenticationRequestDto();
        twoFactorAuthenticationRequestDto.setCode(code);

        Mockito.when(objectMapper.readValue(ArgumentMatchers.any(Reader.class), ArgumentMatchers.eq(TwoFactorAuthenticationRequestDto.class)))
                .thenReturn(twoFactorAuthenticationRequestDto);

        Mockito.when(messageSource.getMessage(ArgumentMatchers.eq("authentication.exception.missing-credentials"), ArgumentMatchers.any(Object[].class), ArgumentMatchers.any(Locale.class)))
                .thenReturn("");

        // Assert
        Assertions.assertThatExceptionOfType(AuthenticationServiceException.class)
                .isThrownBy(() -> twoFactorAuthenticationProcessingFilter.attemptAuthentication(httpServletRequest, null));
    }

    @Test
    void attemptAuthentication_withValidCredentials_callsAuthenticate() throws IOException, ServletException {
        // Arrange
        HttpServletRequest httpServletRequest = Mockito.mock(HttpServletRequest.class);
        Mockito.when(httpServletRequest.getMethod())
                .thenReturn("POST");
        Mockito.when(httpServletRequest.getHeader(HttpHeaders.AUTHORIZATION))
                .thenReturn("123");

        Mockito.when(httpServletRequest.getReader())
                .thenReturn(Mockito.mock(BufferedReader.class));

        Mockito.when(jwtHeaderExtractor.extract(ArgumentMatchers.anyString()))
                .thenReturn("123");

        var twoFactorAuthenticationRequestDto = new TwoFactorAuthenticationRequestDto();
        twoFactorAuthenticationRequestDto.setCode("code");

        Mockito.when(objectMapper.readValue(ArgumentMatchers.any(Reader.class), ArgumentMatchers.eq(TwoFactorAuthenticationRequestDto.class)))
                .thenReturn(twoFactorAuthenticationRequestDto);

        AuthenticationManager authenticationManager = Mockito.mock(AuthenticationManager.class);
        Mockito.when(authenticationManager.authenticate(ArgumentMatchers.any(TwoFactorAuthenticationToken.class)))
                .thenReturn(Mockito.mock(Authentication.class));

        twoFactorAuthenticationProcessingFilter.setAuthenticationManager(authenticationManager);

        // Act
        twoFactorAuthenticationProcessingFilter.attemptAuthentication(httpServletRequest, null);

        // Assert
        Mockito.verify(authenticationManager, Mockito.atMostOnce())
                .authenticate(ArgumentMatchers.any());
    }
}

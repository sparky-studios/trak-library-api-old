package com.sparkystudios.traklibrary.authentication.server.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sparkystudios.traklibrary.authentication.service.TokenService;
import com.sparkystudios.traklibrary.authentication.service.dto.TokenPayloadDto;
import com.sparkystudios.traklibrary.security.context.UserContext;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collections;
import java.util.List;

@ExtendWith(MockitoExtension.class)
class AuthenticationSuccessHandlerImplTest {

    @Mock
    private TokenService tokenService;

    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private AuthenticationSuccessHandlerImpl authenticationSuccessHandler;

    @Test
    void onAuthenticationSuccess_withNullAuthorities_throwsAuthenticationServiceException() {
        // Arrange
        Authentication authentication = Mockito.mock(Authentication.class);
        Mockito.when(authentication.getPrincipal())
                .thenReturn(new UserContext());

        HttpServletResponse httpServletResponse = Mockito.mock(HttpServletResponse.class);

        // Assert
        Assertions.assertThatExceptionOfType(AuthenticationServiceException.class)
                .isThrownBy(() -> authenticationSuccessHandler.onAuthenticationSuccess(Mockito.mock(HttpServletRequest.class), httpServletResponse, authentication));
    }

    @Test
    void onAuthenticationSuccess_withNoAuthorities_throwsAuthenticationServiceException() {
        // Arrange
        UserContext userContext = new UserContext();
        userContext.setAuthorities(Collections.emptyList());

        Authentication authentication = Mockito.mock(Authentication.class);
        Mockito.when(authentication.getPrincipal())
                .thenReturn(userContext);

        HttpServletResponse httpServletResponse = Mockito.mock(HttpServletResponse.class);

        // Assert
        Assertions.assertThatExceptionOfType(AuthenticationServiceException.class)
                .isThrownBy(() -> authenticationSuccessHandler.onAuthenticationSuccess(Mockito.mock(HttpServletRequest.class), httpServletResponse, authentication));
    }

    @Test
    void onAuthenticationSuccess_withNoRoleAuthority_throwsAuthenticationServiceException() {
        // Arrange
        UserContext userContext = new UserContext();
        userContext.setAuthorities(List.of(new SimpleGrantedAuthority("auth-1"), new SimpleGrantedAuthority("auth-2")));

        Authentication authentication = Mockito.mock(Authentication.class);
        Mockito.when(authentication.getPrincipal())
                .thenReturn(userContext);

        HttpServletResponse httpServletResponse = Mockito.mock(HttpServletResponse.class);

        // Assert
        Assertions.assertThatExceptionOfType(AuthenticationServiceException.class)
                .isThrownBy(() -> authenticationSuccessHandler.onAuthenticationSuccess(Mockito.mock(HttpServletRequest.class), httpServletResponse, authentication));
    }

    @Test
    void onAuthenticationSuccess_withValidData_generatesTokens() throws IOException {
        // Arrange
        Mockito.when(tokenService.createAccessToken(ArgumentMatchers.any(), ArgumentMatchers.anyString(), ArgumentMatchers.anyCollection()))
                .thenReturn("");

        Mockito.when(tokenService.createRefreshToken(ArgumentMatchers.any()))
                .thenReturn("");

        UserContext userContext = new UserContext();
        userContext.setAuthorities(Collections.singletonList(new SimpleGrantedAuthority("ROLE_TEST_USER")));

        Authentication authentication = Mockito.mock(Authentication.class);
        Mockito.when(authentication.getPrincipal())
                .thenReturn(userContext);

        HttpServletResponse httpServletResponse = Mockito.mock(HttpServletResponse.class);
        Mockito.when(httpServletResponse.getWriter())
                .thenReturn(Mockito.mock(PrintWriter.class));

        Mockito.when(objectMapper.writeValueAsString(ArgumentMatchers.any(TokenPayloadDto.class)))
                .thenReturn("");

        // Act
        authenticationSuccessHandler.onAuthenticationSuccess(Mockito.mock(HttpServletRequest.class), httpServletResponse, authentication);

        // Assert
        Mockito.verify(objectMapper, Mockito.atMostOnce())
                .writeValueAsString(ArgumentMatchers.any(TokenPayloadDto.class));

        Mockito.verify(tokenService, Mockito.atMostOnce())
                .createAccessToken(ArgumentMatchers.eq(userContext), ArgumentMatchers.eq("ROLE_TEST_USER"), ArgumentMatchers.anyCollection());

        Mockito.verify(tokenService, Mockito.atMostOnce())
                .createRefreshToken(ArgumentMatchers.any());
    }
}

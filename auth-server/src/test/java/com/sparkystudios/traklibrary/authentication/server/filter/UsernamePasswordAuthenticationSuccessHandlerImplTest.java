package com.sparkystudios.traklibrary.authentication.server.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sparkystudios.traklibrary.authentication.service.dto.TokenPayloadDto;
import com.sparkystudios.traklibrary.security.token.SecurityTokenService;
import com.sparkystudios.traklibrary.security.token.data.SecurityToken;
import com.sparkystudios.traklibrary.security.token.data.UserData;
import com.sparkystudios.traklibrary.security.token.data.UserSecurityRole;
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
import java.util.Set;

@ExtendWith(MockitoExtension.class)
class UsernamePasswordAuthenticationSuccessHandlerImplTest {

    @Mock
    private SecurityTokenService securityTokenService;

    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private UsernamePasswordAuthenticationSuccessHandlerImpl authenticationSuccessHandler;

    @Test
    void onAuthenticationSuccess_withNoRoleAuthority_throwsAuthenticationServiceException() {
        // Arrange
        UserData userData = new UserData();
        userData.setAuthorities(Set.of(new SimpleGrantedAuthority("auth-1"), new SimpleGrantedAuthority("auth-2")));

        Authentication authentication = Mockito.mock(Authentication.class);
        Mockito.when(authentication.getPrincipal())
                .thenReturn(userData);

        HttpServletResponse httpServletResponse = Mockito.mock(HttpServletResponse.class);

        // Assert
        Assertions.assertThatExceptionOfType(AuthenticationServiceException.class)
                .isThrownBy(() -> authenticationSuccessHandler.onAuthenticationSuccess(Mockito.mock(HttpServletRequest.class), httpServletResponse, authentication));
    }

    @Test
    void onAuthenticationSuccess_withNonTwoFactorAuthentication_generatesAccessTokenAndRefreshToken() throws IOException {
        // Arrange
        Mockito.when(securityTokenService.createAccessToken(ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.anyCollection()))
                .thenReturn(Mockito.mock(SecurityToken.class));

        Mockito.when(securityTokenService.createRefreshToken(ArgumentMatchers.any()))
                .thenReturn(Mockito.mock(SecurityToken.class));

        UserData userData = new UserData();
        userData.setAuthorities(Collections.singleton(new SimpleGrantedAuthority(UserSecurityRole.ROLE_USER.name())));
        userData.setUsing2fa(false);

        Authentication authentication = Mockito.mock(Authentication.class);
        Mockito.when(authentication.getPrincipal())
                .thenReturn(userData);

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

        Mockito.verify(securityTokenService, Mockito.atMostOnce())
                .createAccessToken(ArgumentMatchers.eq(userData), ArgumentMatchers.eq(UserSecurityRole.ROLE_USER), ArgumentMatchers.anyCollection());

        Mockito.verify(securityTokenService, Mockito.atMostOnce())
                .createRefreshToken(ArgumentMatchers.any());
    }

    @Test
    void onAuthenticationSuccess_withTwoFactorAuthentication_generatesTwoFactorAuthenticationToken() throws IOException {
        // Arrange
        Mockito.when(securityTokenService.createTwoFactorAuthenticationToken(ArgumentMatchers.any()))
                .thenReturn(Mockito.mock(SecurityToken.class));

        UserData userData = new UserData();
        userData.setAuthorities(Collections.singleton(new SimpleGrantedAuthority(UserSecurityRole.ROLE_USER.name())));
        userData.setUsing2fa(true);

        Authentication authentication = Mockito.mock(Authentication.class);
        Mockito.when(authentication.getPrincipal())
                .thenReturn(userData);

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

        Mockito.verify(securityTokenService, Mockito.atMostOnce())
                .createTwoFactorAuthenticationToken(userData);

        Mockito.verify(securityTokenService, Mockito.never())
                .createRefreshToken(ArgumentMatchers.any());
    }
}

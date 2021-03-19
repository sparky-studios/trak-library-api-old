package com.sparkystudios.traklibrary.authentication.server.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sparkystudios.traklibrary.authentication.service.TokenService;
import com.sparkystudios.traklibrary.authentication.service.dto.TokenPayloadDto;
import com.sparkystudios.traklibrary.security.context.UserContext;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

@ExtendWith(MockitoExtension.class)
class AuthenticationSuccessHandlerImplTest {

    @Mock
    private TokenService tokenService;

    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private AuthenticationSuccessHandlerImpl authenticationSuccessHandler;

    @Test
    void onAuthenticationSuccess_withValidData_generatesTokens() throws IOException {
        // Arrange
        Mockito.when(tokenService.createAccessToken(ArgumentMatchers.any()))
                .thenReturn("");

        Mockito.when(tokenService.createRefreshToken(ArgumentMatchers.any()))
                .thenReturn("");

        Authentication authentication = Mockito.mock(Authentication.class);
        Mockito.when(authentication.getPrincipal())
                .thenReturn(new UserContext());

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
                .createAccessToken(ArgumentMatchers.any());

        Mockito.verify(tokenService, Mockito.atMostOnce())
                .createRefreshToken(ArgumentMatchers.any());
    }
}

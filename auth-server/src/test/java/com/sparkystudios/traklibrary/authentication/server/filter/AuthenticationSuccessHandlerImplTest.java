package com.sparkystudios.traklibrary.authentication.server.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sparkystudios.traklibrary.authentication.service.dto.UserDto;
import com.sparkystudios.traklibrary.authentication.service.factory.JwtFactory;
import com.sparkystudios.traklibrary.security.context.UserContext;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@ExtendWith(MockitoExtension.class)
class AuthenticationSuccessHandlerImplTest {

    @Mock
    private JwtFactory jwtFactory;

    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private AuthenticationSuccessHandlerImpl authenticationSuccessHandler;

    @Test
    void onAuthenticationSuccess_withValidData_generatesTokens() throws IOException, ServletException {
        // Arrange
        Mockito.when(jwtFactory.createAccessToken(ArgumentMatchers.any()))
                .thenReturn("");

        Mockito.when(jwtFactory.createRefreshToken(ArgumentMatchers.any()))
                .thenReturn("");

        Authentication authentication = Mockito.mock(Authentication.class);
        Mockito.when(authentication.getPrincipal())
                .thenReturn(new UserContext());

        // Act
        authenticationSuccessHandler.onAuthenticationSuccess(Mockito.mock(HttpServletRequest.class), Mockito.mock(HttpServletResponse.class), authentication);

        // Assert
        Mockito.verify(jwtFactory, Mockito.atMostOnce())
                .createAccessToken(ArgumentMatchers.any());

        Mockito.verify(jwtFactory, Mockito.atMostOnce())
                .createRefreshToken(ArgumentMatchers.any());
    }
}

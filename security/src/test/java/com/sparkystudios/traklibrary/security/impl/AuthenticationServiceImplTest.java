package com.sparkystudios.traklibrary.security.impl;

import com.sparkystudios.traklibrary.security.token.authentication.JwtAuthenticationToken;
import com.sparkystudios.traklibrary.security.token.data.SecurityToken;
import com.sparkystudios.traklibrary.security.token.data.UserSecurityRole;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Collections;

@ExtendWith(MockitoExtension.class)
class AuthenticationServiceImplTest {

    @InjectMocks
    private AuthenticationServiceImpl authenticationService;

    @Test
    void getToken_withNonJwtAuthenticationToken_returnsFalse() {
        // Arrange
        SecurityContext securityContext = Mockito.mock(SecurityContext.class);
        Mockito.when(securityContext.getAuthentication())
                .thenReturn(Mockito.mock(Authentication.class));

        SecurityContextHolder.setContext(securityContext);

        // Act
        String result = authenticationService.getToken();

        // Assert
        Assertions.assertEquals("", result, "The token should be false if no jwt authentication token is found.");
    }

    @Test
    void getToken_withAuthenticationUser_returnsAuthenticationUserToken() {
        // Arrange
        JwtAuthenticationToken token = new JwtAuthenticationToken("token-123");

        SecurityContext securityContext = Mockito.mock(SecurityContext.class);
        Mockito.when(securityContext.getAuthentication())
                .thenReturn(token);

        SecurityContextHolder.setContext(securityContext);

        // Act
        String result = authenticationService.getToken();

        // Assert
        Assertions.assertEquals(token.getCredentials(), result, "The token should be equal to the authenticated user token.");
    }

    @Test
    void isCurrentAuthenticatedUser_withNonJwtAuthenticationToken_returnsFalse() {
        // Arrange
        SecurityContext securityContext = Mockito.mock(SecurityContext.class);
        Mockito.when(securityContext.getAuthentication())
                .thenReturn(Mockito.mock(Authentication.class));

        SecurityContextHolder.setContext(securityContext);

        // Act
        boolean result = authenticationService.isCurrentAuthenticatedUser(0L);

        // Assert
        Assertions.assertFalse(result, "Authentication should be false if the token isn't a JWT authentication token.");
    }

    @Test
    void isCurrentAuthenticatedUser_withAdminRole_returnsTrue() {
        // Arrange
        SecurityToken securityToken = Mockito.mock(SecurityToken.class);
        Mockito.when(securityToken.getRole())
                .thenReturn(new SimpleGrantedAuthority(UserSecurityRole.ROLE_ADMIN.name()));

        JwtAuthenticationToken token =
                new JwtAuthenticationToken(securityToken, null);

        SecurityContext securityContext = Mockito.mock(SecurityContext.class);
        Mockito.when(securityContext.getAuthentication())
                .thenReturn(token);

        SecurityContextHolder.setContext(securityContext);

        // Act
        boolean result = authenticationService.isCurrentAuthenticatedUser(0L);

        // Assert
        Assertions.assertTrue(result, "Authentication should always be true if the user has admin privileges.");
    }

    @Test
    void isCurrentAuthenticatedUser_withDifferentUser_returnsFalse() {
        // Arrange
        SecurityToken securityToken = Mockito.mock(SecurityToken.class);
        Mockito.when(securityToken.getUserId())
                .thenReturn(1L);
        Mockito.when(securityToken.getRole())
                .thenReturn(new SimpleGrantedAuthority(UserSecurityRole.ROLE_USER.name()));

        JwtAuthenticationToken token = new JwtAuthenticationToken(securityToken, Collections.emptySet());

        SecurityContext securityContext = Mockito.mock(SecurityContext.class);
        Mockito.when(securityContext.getAuthentication())
                .thenReturn(token);

        SecurityContextHolder.setContext(securityContext);

        // Act
        boolean result = authenticationService.isCurrentAuthenticatedUser(0L);

        // Assert
        Assertions.assertFalse(result, "Authentication should be false if the user has a different user ID.");
    }

    @Test
    void isCurrentAuthenticatedUser_withSameUser_returnsTrue() {
        // Arrange
        SecurityToken securityToken = Mockito.mock(SecurityToken.class);
        Mockito.when(securityToken.getUserId())
                .thenReturn(1L);
        Mockito.when(securityToken.getRole())
                .thenReturn(new SimpleGrantedAuthority(UserSecurityRole.ROLE_USER.name()));

        JwtAuthenticationToken token = new JwtAuthenticationToken(securityToken, Collections.emptySet());

        SecurityContext securityContext = Mockito.mock(SecurityContext.class);
        Mockito.when(securityContext.getAuthentication())
                .thenReturn(token);

        SecurityContextHolder.setContext(securityContext);

        // Act
        boolean result = authenticationService.isCurrentAuthenticatedUser(1L);

        // Assert
        Assertions.assertTrue(result, "Authentication should be true if the user has the same user ID.");
    }
}

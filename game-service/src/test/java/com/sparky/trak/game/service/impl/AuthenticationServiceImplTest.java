package com.sparky.trak.game.service.impl;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Collections;

@ExtendWith(MockitoExtension.class)
public class AuthenticationServiceImplTest {

    @InjectMocks
    private AuthenticationServiceImpl authenticationService;

    @Test
    public void isCurrentAuthenticatedUser_withNonUsernamePasswordAuthenticationToken_returnsFalse() {
        // Arrange
        SecurityContext securityContext = Mockito.mock(SecurityContext.class);
        Mockito.when(securityContext.getAuthentication())
                .thenReturn(Mockito.mock(Authentication.class));

        SecurityContextHolder.setContext(securityContext);

        // Act
        boolean result = authenticationService.isCurrentAuthenticatedUser(0L);

        // Assert
        Assertions.assertFalse(result, "Authentication should be false if the token isn't a username and password.");
    }

    @Test
    public void isCurrentAuthenticatedUser_withAdminRole_returnsTrue() {
        // Arrange
        UsernamePasswordAuthenticationToken token =
                new UsernamePasswordAuthenticationToken(null, null, Collections.singleton(new SimpleGrantedAuthority("ROLE_ADMIN")));

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
    public void isCurrentAuthenticatedUser_withDifferentUser_returnsFalse() {
        // Arrange
        UsernamePasswordAuthenticationToken token =
                new UsernamePasswordAuthenticationToken(null, null, Collections.emptySet());
        token.setDetails(1L);

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
    public void isCurrentAuthenticatedUser_withSameUser_returnsTrue() {
        // Arrange
        UsernamePasswordAuthenticationToken token =
                new UsernamePasswordAuthenticationToken(null, null, Collections.emptySet());
        token.setDetails(1L);

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

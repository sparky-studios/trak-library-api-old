package com.sparkystudios.traklibrary.authentication.service.impl;

import com.sparkystudios.traklibrary.security.context.UserContext;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@ExtendWith(MockitoExtension.class)
class TokenServiceJwtImplTest {

    @InjectMocks
    private TokenServiceJwtImpl tokenService;

    @Test
    void createAccessToken_withNullAuthorities_throwsIllegalArgumentException() {
        // Arrange
        UserContext userContext = new UserContext();
        userContext.setAuthorities(null);

        Iterable<String> scopes = Collections.emptyList();

        // Assert
        Assertions.assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> tokenService.createAccessToken(userContext, "", scopes));
    }

    @Test
    void createAccessToken_withEmptyAuthorities_throwsIllegalArgumentException() {
        // Arrange
        UserContext userContext = new UserContext();
        userContext.setAuthorities(Collections.emptyList());

        Iterable<String> scopes = Collections.emptyList();

        // Assert
        Assertions.assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> tokenService.createAccessToken(userContext, "", scopes));
    }

    @Test
    void createAccessToken_withValidData_createsValidToken() {
        // Arrange
        UserContext userContext = new UserContext();
        userContext.setUserId(1L);
        userContext.setUsername("username");
        userContext.setVerified(true);
        userContext.setAuthorities(List.of(new SimpleGrantedAuthority("ROLE_TEST_USER")));

        String role = "ROLE_TEST_USER";
        Collection<String> scopes = List.of("scope1", "scope2");

        tokenService.setExpiryTime(10000L);

        TokenServiceJwtImpl tokenServiceSpy = Mockito.spy(tokenService);
        Mockito.doReturn("token")
                .when(tokenServiceSpy).createToken(userContext.getUsername(), "ROLE_TEST_USER", scopes, userContext.getUserId(), userContext.isVerified());

        // Act
        String result = tokenServiceSpy.createAccessToken(userContext, role, scopes);

        // Assert
        Assertions.assertThat(result).isEqualTo("token");
    }

    @Test
    void createRefreshToken_withValidData_createValidToken() {
        // Arrange
        UserContext userContext = new UserContext();
        userContext.setUserId(1L);
        userContext.setUsername("username");
        userContext.setVerified(true);

        tokenService.setRefreshExpiryTime(10000L);

        TokenServiceJwtImpl tokenServiceSpy = Mockito.spy(tokenService);
        Mockito.doReturn("token")
                .when(tokenServiceSpy).createToken(userContext.getUsername(), "ROLE_TOKEN_REFRESH", Collections.emptyList(), userContext.getUserId(), userContext.isVerified());

        // Act
        String result = tokenServiceSpy.createRefreshToken(userContext);

        // Assert
        Assertions.assertThat(result).isEqualTo("token");
    }
}

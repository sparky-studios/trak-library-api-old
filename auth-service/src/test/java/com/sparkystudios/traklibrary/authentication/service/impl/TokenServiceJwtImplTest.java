package com.sparkystudios.traklibrary.authentication.service.impl;

import com.sparkystudios.traklibrary.security.context.UserContext;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

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

        // Assert
        Assertions.assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> tokenService.createAccessToken(userContext));
    }

    @Test
    void createAccessToken_withEmptyAuthorities_throwsIllegalArgumentException() {
        // Arrange
        UserContext userContext = new UserContext();
        userContext.setAuthorities(Collections.emptyList());

        // Assert
        Assertions.assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> tokenService.createAccessToken(userContext));
    }

    @Test
    void createAccessToken_withValidData_containsCorrectClaimsAndJwtData() {
        // Arrange
        UserContext userContext = new UserContext();
        userContext.setUserId(1L);
        userContext.setUsername("username");
        userContext.setVerified(true);
        userContext.setAuthorities(List.of(new SimpleGrantedAuthority("ROLE_TEST_USER")));

        tokenService.setSecretKey("123");
        tokenService.setExpiryTime(10000L);

        // Act
        String result = tokenService.createAccessToken(userContext);

        // Assert
        Claims claims = Jwts.parser()
                .setSigningKey(tokenService.getSecretKey().getBytes())
                .parseClaimsJws(result)
                .getBody();

        Assertions.assertThat(claims.getIssuer()).isEqualTo("Trak Library");
        Assertions.assertThat(claims.getSubject()).isEqualTo(userContext.getUsername());
        Assertions.assertThat(claims.get("scopes"))
                .isEqualTo(userContext.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.toList()));
        Assertions.assertThat(claims.get("userId", Long.class)).isEqualTo(userContext.getUserId());
        Assertions.assertThat(claims.get("verified", Boolean.class)).isTrue();
        Assertions.assertThat(claims.getAudience()).isEqualTo("https://api.traklibrary.com");
    }

    @Test
    void createRefreshToken_withValidData_containsUserDataAndTokenRefreshScope() {
        // Arrange
        UserContext userContext = new UserContext();
        userContext.setUserId(1L);
        userContext.setUsername("username");
        userContext.setVerified(true);
        userContext.setAuthorities(List.of(new SimpleGrantedAuthority("ROLE_TEST_USER")));

        tokenService.setSecretKey("123");
        tokenService.setRefreshExpiryTime(10000L);

        // Act
        String result = tokenService.createRefreshToken(userContext);

        // Assert
        Claims claims = Jwts.parser()
                .setSigningKey(tokenService.getSecretKey().getBytes())
                .parseClaimsJws(result)
                .getBody();

        Assertions.assertThat(claims.getIssuer()).isEqualTo("Trak Library");
        Assertions.assertThat(claims.getSubject()).isEqualTo(userContext.getUsername());
        Assertions.assertThat(claims.get("scopes")).isEqualTo(List.of("TOKEN_REFRESH"));
        Assertions.assertThat(claims.get("userId", Long.class)).isEqualTo(userContext.getUserId());
        Assertions.assertThat(claims.get("verified", Boolean.class)).isTrue();
        Assertions.assertThat(claims.getAudience()).isEqualTo("https://api.traklibrary.com");
    }
}

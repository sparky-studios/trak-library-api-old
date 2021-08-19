package com.sparkystudios.traklibrary.security.token.adapter;

import io.jsonwebtoken.Claims;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.*;
import java.util.stream.Collectors;

class JwtSecurityTokenAdapterTest {

    @Test
    void getId_withClaimsData_returnsCorrectId() {
        // Arrange
        String id = "token-id";

        Claims claims = Mockito.mock(Claims.class);
        Mockito.when(claims.getId())
                .thenReturn(id);

        JwtSecurityTokenAdapter jwtSecurityTokenAdapter = new JwtSecurityTokenAdapter(claims, "");

        // Act
        String result = jwtSecurityTokenAdapter.getId();

        // Assert
        Assertions.assertThat(result)
                .isEqualTo(id);
    }

    @Test
    void getToken_withToken_returnsToken() {
        // Arrange
        String token = "token";

        JwtSecurityTokenAdapter jwtSecurityTokenAdapter = new JwtSecurityTokenAdapter(null, token);

        // Act
        String result = jwtSecurityTokenAdapter.getToken();

        // Assert
        Assertions.assertThat(result)
                .isEqualTo(token);
    }

    @Test
    void getUsername_withClaimsSubject_returnsCorrectUsername() {
        // Arrange
        String username = "username";

        Claims claims = Mockito.mock(Claims.class);
        Mockito.when(claims.getSubject())
                .thenReturn(username);

        JwtSecurityTokenAdapter jwtSecurityTokenAdapter = new JwtSecurityTokenAdapter(claims, "");

        // Act
        String result = jwtSecurityTokenAdapter.getUsername();

        // Assert
        Assertions.assertThat(result)
                .isEqualTo(username);
    }

    @Test
    void getUserId_withClaimsWithUserId_returnsCorrectUserId() {
        // Arrange
        long userId = 5L;

        Claims claims = Mockito.mock(Claims.class);
        Mockito.when(claims.get("userId", Long.class))
                .thenReturn(userId);

        JwtSecurityTokenAdapter jwtSecurityTokenAdapter = new JwtSecurityTokenAdapter(claims, "");

        // Act
        long result = jwtSecurityTokenAdapter.getUserId();

        // Assert
        Assertions.assertThat(result)
                .isEqualTo(userId);
    }

    @Test
    void isVerified_withClaimsWithVerified_returnsCorrectVerificationState() {
        // Arrange
        boolean verified = true;

        Claims claims = Mockito.mock(Claims.class);
        Mockito.when(claims.get("verified", Boolean.class))
                .thenReturn(verified);

        JwtSecurityTokenAdapter jwtSecurityTokenAdapter = new JwtSecurityTokenAdapter(claims, "");

        // Act
        boolean result = jwtSecurityTokenAdapter.isVerified();

        // Assert
        Assertions.assertThat(result)
                .isEqualTo(verified);
    }

    @Test
    void getIssuedAt_withValidIssuedAtDate_returnsIssuedAtDateInCorrectTimeZone() {
        // Arrange
        Date issuedAt = new Date();

        Claims claims = Mockito.mock(Claims.class);
        Mockito.when(claims.getIssuedAt())
                .thenReturn(issuedAt);

        JwtSecurityTokenAdapter jwtSecurityTokenAdapter = new JwtSecurityTokenAdapter(claims, "");

        // Act
        LocalDateTime result = jwtSecurityTokenAdapter.getIssuedAt();

        // Assert
        Assertions.assertThat(result)
                .isEqualTo(issuedAt.toInstant().atOffset(ZoneOffset.UTC).toLocalDateTime());
    }

    @Test
    void getExpiry_withValidExpiryDate_returnsExpiryDateInCorrectTimeZone() {
        // Arrange
        Date expiry = new Date();

        Claims claims = Mockito.mock(Claims.class);
        Mockito.when(claims.getExpiration())
                .thenReturn(expiry);

        JwtSecurityTokenAdapter jwtSecurityTokenAdapter = new JwtSecurityTokenAdapter(claims, "");

        // Act
        LocalDateTime result = jwtSecurityTokenAdapter.getExpiry();

        // Assert
        Assertions.assertThat(result)
                .isEqualTo(expiry.toInstant().atOffset(ZoneOffset.UTC).toLocalDateTime());
    }

    @Test
    void getRole_withClaimsWithRole_returnsRoleAsSimpleGrantedAuthority() {
        // Arrange
        String role = "role";

        Claims claims = Mockito.mock(Claims.class);
        Mockito.when(claims.get("role", String.class))
                .thenReturn(role);

        JwtSecurityTokenAdapter jwtSecurityTokenAdapter = new JwtSecurityTokenAdapter(claims, "");

        // Act
        GrantedAuthority result = jwtSecurityTokenAdapter.getRole();

        // Assert
        Assertions.assertThat(result)
                .isInstanceOf(SimpleGrantedAuthority.class)
                .matches(c -> Objects.equals(c.getAuthority(), role));
    }

    @Test
    void getAuthorities_withClaimsWithScope_returnsAuthoritiesAsSimpleGrantedAuthorities() {
        // Arrange
        Collection<String> scopes = List.of("scope1", "scope2");

        Claims claims = Mockito.mock(Claims.class);
        Mockito.when(claims.get("scope"))
                .thenReturn(scopes);

        JwtSecurityTokenAdapter jwtSecurityTokenAdapter = new JwtSecurityTokenAdapter(claims, "");

        // Act
        Collection<GrantedAuthority> result = jwtSecurityTokenAdapter.getAuthorities();

        // Assert
        Assertions.assertThat(result)
                .isNotEmpty()
                .containsAll(scopes.stream().map(SimpleGrantedAuthority::new).collect(Collectors.toList()));
    }
}

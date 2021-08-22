package com.sparkystudios.traklibrary.security.provider;

import com.sparkystudios.traklibrary.security.token.SecurityTokenService;
import com.sparkystudios.traklibrary.security.token.authentication.JwtAuthenticationToken;
import com.sparkystudios.traklibrary.security.token.data.SecurityToken;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Collections;
import java.util.List;

@ExtendWith(MockitoExtension.class)
class JwtAuthenticationProviderTest {

    @Mock
    private SecurityTokenService securityTokenService;

    @InjectMocks
    private JwtAuthenticationProvider jwtAuthenticationProvider;

    @Test
    void authenticate_withDefaultData_setsJwtAuthenticationToken() {
        // Arrange
        SecurityToken securityToken = Mockito.spy(SecurityToken.class);
        Mockito.doReturn(new SimpleGrantedAuthority("role"))
                .when(securityToken).getRole();
        Mockito.doReturn(Collections.singletonList(new SimpleGrantedAuthority("auth")))
                .when(securityToken).getAuthorities();

        Mockito.when(securityTokenService.getToken(ArgumentMatchers.anyString()))
                .thenReturn(securityToken);

        Authentication authentication = Mockito.spy(Authentication.class);
        Mockito.doReturn("access_token")
                .when(authentication).getCredentials();

        // Act
        JwtAuthenticationToken result = (JwtAuthenticationToken) jwtAuthenticationProvider.authenticate(authentication);

        // Assert
        Assertions.assertThat(result.getPrincipal())
                .isEqualTo(securityToken);
        Assertions.assertThat(result.getAuthorities())
                .isEqualTo(List.of(new SimpleGrantedAuthority("role"), new SimpleGrantedAuthority("auth")));
    }

    @Test
    void supports_withNonJwtAuthenticationToken_returnsFalse() {
        // Arrange
        boolean result = jwtAuthenticationProvider.supports(Integer.class);

        // Assert
        Assertions.assertThat(result).isFalse();
    }

    @Test
    void supports_withJwtAuthenticationToken_returnsTrue() {
        // Arrange
        boolean result = jwtAuthenticationProvider.supports(JwtAuthenticationToken.class);

        // Assert
        Assertions.assertThat(result).isTrue();
    }
}

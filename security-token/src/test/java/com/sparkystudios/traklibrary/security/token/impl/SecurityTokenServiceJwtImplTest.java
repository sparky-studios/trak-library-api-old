package com.sparkystudios.traklibrary.security.token.impl;

import com.sparkystudios.traklibrary.security.token.data.SecurityToken;
import com.sparkystudios.traklibrary.security.token.data.UserData;
import com.sparkystudios.traklibrary.security.token.data.UserSecurityRole;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;

@ExtendWith(MockitoExtension.class)
class SecurityTokenServiceJwtImplTest {

    @InjectMocks
    private SecurityTokenServiceJwtImpl securityTokenService;

    @Test
    void createAccessToken_withNullAuthorities_throwsIllegalArgumentException() {
        // Arrange
        UserData userData = new UserData();
        userData.setAuthorities(null);

        Iterable<String> scopes = Collections.emptyList();

        // Assert
        Assertions.assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> securityTokenService.createAccessToken(userData, UserSecurityRole.ROLE_USER, scopes));
    }

    @Test
    void createAccessToken_withEmptyAuthorities_throwsIllegalArgumentException() {
        // Arrange
        UserData userData = new UserData();
        userData.setAuthorities(Collections.emptyList());

        Iterable<String> scopes = Collections.emptyList();

        // Assert
        Assertions.assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> securityTokenService.createAccessToken(userData, UserSecurityRole.ROLE_USER, scopes));
    }

    @Test
    void createAccessToken_withValidData_createsValidToken() {
        // Arrange
        UserData userData = new UserData();
        userData.setUserId(1L);
        userData.setUsername("username");
        userData.setVerified(true);
        userData.setAuthorities(Set.of(new SimpleGrantedAuthority(UserSecurityRole.ROLE_USER.name())));

        Collection<String> scopes = List.of("scope1", "scope2");

        securityTokenService.setExpiryTime(10000L);

        SecurityTokenServiceJwtImpl tokenServiceSpy = Mockito.spy(securityTokenService);
        Mockito.doReturn(Mockito.mock(SecurityToken.class))
                .when(tokenServiceSpy).createToken(userData.getUsername(), UserSecurityRole.ROLE_USER, scopes, userData.getUserId(), userData.isVerified(), 10000L);

        // Act
        SecurityToken result = tokenServiceSpy.createAccessToken(userData, UserSecurityRole.ROLE_USER, scopes);

        // Assert
        Assertions.assertThat(result).isNotNull();
    }

    @Test
    void createTwoFactorAuthenticationToken_withValidData_createValidToken() {
        // Arrange
        UserData userData = new UserData();
        userData.setUserId(1L);
        userData.setUsername("username");
        userData.setVerified(true);

        securityTokenService.setTwoFactorAuthExpiryTime(10000L);

        SecurityTokenServiceJwtImpl tokenServiceSpy = Mockito.spy(securityTokenService);
        Mockito.doReturn(Mockito.mock(SecurityToken.class))
                .when(tokenServiceSpy).createToken(userData.getUsername(), UserSecurityRole.ROLE_TWO_FACTOR_AUTHENTICATION_TOKEN, Collections.emptyList(), userData.getUserId(), userData.isVerified(), 10000L);

        // Act
        SecurityToken result = tokenServiceSpy.createTwoFactorAuthenticationToken(userData);

        // Assert
        Assertions.assertThat(result).isNotNull();
    }

    @Test
    void createRefreshToken_withValidData_createValidToken() {
        // Arrange
        UserData userData = new UserData();
        userData.setUserId(1L);
        userData.setUsername("username");
        userData.setVerified(true);

        securityTokenService.setRefreshExpiryTime(10000L);

        SecurityTokenServiceJwtImpl tokenServiceSpy = Mockito.spy(securityTokenService);
        Mockito.doReturn(Mockito.mock(SecurityToken.class))
                .when(tokenServiceSpy).createToken(userData.getUsername(), UserSecurityRole.ROLE_TOKEN_REFRESH, Collections.emptyList(), userData.getUserId(), userData.isVerified(), 10000L);

        // Act
        SecurityToken result = tokenServiceSpy.createRefreshToken(userData);

        // Assert
        Assertions.assertThat(result).isNotNull();
    }
}

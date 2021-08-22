package com.sparkystudios.traklibrary.authentication.server.provider;

import com.sparkystudios.traklibrary.authentication.service.dto.UserDto;
import com.sparkystudios.traklibrary.security.token.SecurityTokenService;
import com.sparkystudios.traklibrary.security.token.authentication.JwtAuthenticationToken;
import com.sparkystudios.traklibrary.security.token.authentication.TwoFactorAuthenticationToken;
import com.sparkystudios.traklibrary.security.token.data.SecurityToken;
import com.sparkystudios.traklibrary.security.token.data.UserData;
import com.sparkystudios.traklibrary.security.token.data.UserSecurityRole;
import dev.samstevens.totp.code.CodeVerifier;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.MessageSource;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.Locale;
import java.util.Set;

@ExtendWith(MockitoExtension.class)
class TwoFactorAuthenticationProviderTest {

    @Mock
    private UserDetailsService userDetailsService;

    @Mock
    private SecurityTokenService securityTokenService;

    @Mock
    private CodeVerifier codeVerifier;

    @Mock
    private MessageSource messageSource;

    @InjectMocks
    private TwoFactorAuthenticationProvider twoFactorAuthenticationProvider;

    @Test
    void authenticate_withInvalidTwoFactorAuthenticationCode_throwsBadCredentialsException() {
        // Arrange
        SecurityToken securityToken = Mockito.mock(SecurityToken.class);
        Mockito.when(securityToken.getUsername())
                .thenReturn("username");

        Mockito.when(securityTokenService.getToken(ArgumentMatchers.anyString()))
                .thenReturn(securityToken);

        UserDto userDto = new UserDto();
        userDto.setTwoFactorAuthenticationSecret("secret");

        Mockito.when(userDetailsService.loadUserByUsername(ArgumentMatchers.anyString()))
                .thenReturn(userDto);

        Mockito.when(codeVerifier.isValidCode(ArgumentMatchers.anyString(), ArgumentMatchers.anyString()))
                .thenReturn(false);

        Mockito.when(messageSource.getMessage(ArgumentMatchers.eq("authentication.exception.bad-2fa-credentials"), ArgumentMatchers.any(Object[].class), ArgumentMatchers.any(Locale.class)))
                .thenReturn("");

        TwoFactorAuthenticationToken twoFactorAuthenticationToken =
                new TwoFactorAuthenticationToken("jwt", "code");

        // Assert
        Assertions.assertThatExceptionOfType(BadCredentialsException.class)
                .isThrownBy(() -> twoFactorAuthenticationProvider.authenticate(twoFactorAuthenticationToken));
    }

    @Test
    void authenticate_withValidTwoFactorAuthenticationCode_returnsTwoFactorAuthenticationTokenWithUserDetails() {
        // Arrange
        SecurityToken securityToken = Mockito.mock(SecurityToken.class);
        Mockito.when(securityToken.getUsername())
                .thenReturn("username");

        Mockito.when(securityTokenService.getToken(ArgumentMatchers.anyString()))
                .thenReturn(securityToken);

        Set<GrantedAuthority> authorities = Set.of(new SimpleGrantedAuthority(UserSecurityRole.ROLE_USER.name()));

        UserDto userDto = new UserDto();
        userDto.setId(1L);
        userDto.setPassword("password");
        userDto.setVerified(true);
        userDto.setAuthorities(authorities);
        userDto.setTwoFactorAuthenticationSecret("secret");

        Mockito.when(userDetailsService.loadUserByUsername(ArgumentMatchers.anyString()))
                .thenReturn(userDto);

        Mockito.when(codeVerifier.isValidCode(ArgumentMatchers.anyString(), ArgumentMatchers.anyString()))
                .thenReturn(true);

        TwoFactorAuthenticationToken twoFactorAuthenticationToken =
                new TwoFactorAuthenticationToken("username", "password");

        // Act
        TwoFactorAuthenticationToken result =
                (TwoFactorAuthenticationToken) twoFactorAuthenticationProvider.authenticate(twoFactorAuthenticationToken);

        // Assert
        UserData userData = (UserData) result.getPrincipal();

        Assertions.assertThat(userData.getUserId()).isEqualTo(userDto.getId());
        Assertions.assertThat(userData.getUsername()).isEqualTo(userDto.getUsername());
        Assertions.assertThat(userData.isVerified()).isTrue();
        Assertions.assertThat(userData.getAuthorities()).isEqualTo(authorities);
        Assertions.assertThat(result.getCredentials()).isNull();
        Assertions.assertThat(result.getAuthorities().iterator().next()).isEqualTo(authorities.iterator().next());
    }

    @Test
    void supports_withNonJwtAuthenticationToken_returnsFalse() {
        // Act
        boolean result = twoFactorAuthenticationProvider.supports(Integer.class);

        // Assert
        Assertions.assertThat(result).isFalse();
    }

    @Test
    void supports_withTwoFactorAuthenticationToken_returnsTrue() {
        // Act
        boolean result = twoFactorAuthenticationProvider.supports(TwoFactorAuthenticationToken.class);

        // Assert
        Assertions.assertThat(result).isTrue();
    }
}

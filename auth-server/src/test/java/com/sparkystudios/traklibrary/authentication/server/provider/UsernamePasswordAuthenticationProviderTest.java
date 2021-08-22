package com.sparkystudios.traklibrary.authentication.server.provider;

import com.sparkystudios.traklibrary.authentication.service.dto.UserDto;
import com.sparkystudios.traklibrary.security.token.data.UserData;
import com.sparkystudios.traklibrary.security.token.data.UserSecurityRole;
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
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Collections;
import java.util.Locale;
import java.util.Set;

@ExtendWith(MockitoExtension.class)
class UsernamePasswordAuthenticationProviderTest {

    @Mock
    private UserDetailsService userDetailsService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private MessageSource messageSource;

    @InjectMocks
    private UsernamePasswordAuthenticationProvider usernamePasswordAuthenticationProvider;

    @Test
    void authenticate_withNonMatchingPasswords_throwsBadCredentialsException() {
        // Arrange
        UserDto userDto = new UserDto();
        userDto.setPassword("password");

        Mockito.when(userDetailsService.loadUserByUsername(ArgumentMatchers.anyString()))
                .thenReturn(userDto);

        Mockito.when(passwordEncoder.matches(ArgumentMatchers.anyString(), ArgumentMatchers.anyString()))
                .thenReturn(false);

        Mockito.when(messageSource.getMessage(ArgumentMatchers.eq("authentication.exception.bad-credentials"), ArgumentMatchers.any(Object[].class), ArgumentMatchers.any(Locale.class)))
                .thenReturn("");

        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken =
                new UsernamePasswordAuthenticationToken("username", "password");

        // Assert
        Assertions.assertThatExceptionOfType(BadCredentialsException.class)
                .isThrownBy(() -> usernamePasswordAuthenticationProvider.authenticate(usernamePasswordAuthenticationToken));
    }

    @Test
    void authenticate_withNullAuthorities_throwsInsufficientAuthenticationException() {
        // Arrange
        UserDto userDto = new UserDto();
        userDto.setPassword("password");
        userDto.setAuthorities(null);

        Mockito.when(userDetailsService.loadUserByUsername(ArgumentMatchers.anyString()))
                .thenReturn(userDto);

        Mockito.when(passwordEncoder.matches(ArgumentMatchers.anyString(), ArgumentMatchers.anyString()))
                .thenReturn(true);

        Mockito.when(messageSource.getMessage(ArgumentMatchers.eq("authentication.exception.insufficient-roles"), ArgumentMatchers.any(Object[].class), ArgumentMatchers.any(Locale.class)))
                .thenReturn("");

        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken =
                new UsernamePasswordAuthenticationToken("username", "password");

        // Assert
        Assertions.assertThatExceptionOfType(InsufficientAuthenticationException.class)
                .isThrownBy(() -> usernamePasswordAuthenticationProvider.authenticate(usernamePasswordAuthenticationToken));
    }

    @Test
    void authenticate_withNoAuthorities_throwsInsufficientAuthenticationException() {
        // Arrange
        UserDto userDto = new UserDto();
        userDto.setPassword("password");
        userDto.setAuthorities(Collections.emptySet());

        Mockito.when(userDetailsService.loadUserByUsername(ArgumentMatchers.anyString()))
                .thenReturn(userDto);

        Mockito.when(passwordEncoder.matches(ArgumentMatchers.anyString(), ArgumentMatchers.anyString()))
                .thenReturn(true);

        Mockito.when(messageSource.getMessage(ArgumentMatchers.eq("authentication.exception.insufficient-roles"), ArgumentMatchers.any(Object[].class), ArgumentMatchers.any(Locale.class)))
                .thenReturn("");

        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken =
                new UsernamePasswordAuthenticationToken("username", "password");

        // Assert
        Assertions.assertThatExceptionOfType(InsufficientAuthenticationException.class)
                .isThrownBy(() -> usernamePasswordAuthenticationProvider.authenticate(usernamePasswordAuthenticationToken));
    }

    @Test
    void authenticate_withValidCredentialsAndAuthorities_returnsUsernameAndPasswordAuthenticationTokenWithUserDetails() {
        // Arrange
        Set<GrantedAuthority> authorities = Set.of(new SimpleGrantedAuthority(UserSecurityRole.ROLE_USER.name()));

        UserDto userDto = new UserDto();
        userDto.setId(1L);
        userDto.setPassword("password");
        userDto.setVerified(true);
        userDto.setAuthorities(authorities);

        Mockito.when(userDetailsService.loadUserByUsername(ArgumentMatchers.anyString()))
                .thenReturn(userDto);

        Mockito.when(passwordEncoder.matches(ArgumentMatchers.anyString(), ArgumentMatchers.anyString()))
                .thenReturn(true);

        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken =
                new UsernamePasswordAuthenticationToken("username", "password");

        // Act
        UsernamePasswordAuthenticationToken result =
                (UsernamePasswordAuthenticationToken) usernamePasswordAuthenticationProvider.authenticate(usernamePasswordAuthenticationToken);

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
    void supports_withNonUsernamePasswordAuthenticationToken_returnsFalse() {
        // Act
        boolean result = usernamePasswordAuthenticationProvider.supports(Integer.class);

        // Assert
        Assertions.assertThat(result).isFalse();
    }

    @Test
    void supports_withUsernamePasswordAuthenticationToken_returnsTrue() {
        // Act
        boolean result = usernamePasswordAuthenticationProvider.supports(UsernamePasswordAuthenticationToken.class);

        // Assert
        Assertions.assertThat(result).isTrue();
    }
}

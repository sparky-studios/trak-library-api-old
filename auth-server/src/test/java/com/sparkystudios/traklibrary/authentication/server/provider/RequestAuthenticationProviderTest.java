package com.sparkystudios.traklibrary.authentication.server.provider;

import com.sparkystudios.traklibrary.authentication.service.UserService;
import com.sparkystudios.traklibrary.authentication.service.dto.UserDto;
import com.sparkystudios.traklibrary.security.context.UserContext;
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
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Collections;
import java.util.List;
import java.util.Locale;

@ExtendWith(MockitoExtension.class)
class RequestAuthenticationProviderTest {

    @Mock
    private UserService userService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private MessageSource messageSource;

    @InjectMocks
    private RequestAuthenticationProvider requestAuthenticationProvider;

    @Test
    void authenticate_withNonMatchingPasswords_throwsBadCredentialsException() {
        // Arrange
        UserDto userDto = new UserDto();
        userDto.setPassword("password");

        Mockito.when(userService.loadUserByUsername(ArgumentMatchers.anyString()))
                .thenReturn(userDto);

        Mockito.when(passwordEncoder.matches(ArgumentMatchers.anyString(), ArgumentMatchers.anyString()))
                .thenReturn(false);

        Mockito.when(messageSource.getMessage(ArgumentMatchers.eq("authentication.exception.bad-credentials"), ArgumentMatchers.any(Object[].class), ArgumentMatchers.any(Locale.class)))
                .thenReturn("");

        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken =
                new UsernamePasswordAuthenticationToken("username", "password");

        // Assert
        Assertions.assertThatExceptionOfType(BadCredentialsException.class)
                .isThrownBy(() -> requestAuthenticationProvider.authenticate(usernamePasswordAuthenticationToken));
    }

    @Test
    void authenticate_withNullAuthorities_throwsInsufficientAuthenticationException() {
        // Arrange
        UserDto userDto = new UserDto();
        userDto.setPassword("password");
        userDto.setAuthorities(null);

        Mockito.when(userService.loadUserByUsername(ArgumentMatchers.anyString()))
                .thenReturn(userDto);

        Mockito.when(passwordEncoder.matches(ArgumentMatchers.anyString(), ArgumentMatchers.anyString()))
                .thenReturn(true);

        Mockito.when(messageSource.getMessage(ArgumentMatchers.eq("authentication.exception.insufficient-roles"), ArgumentMatchers.any(Object[].class), ArgumentMatchers.any(Locale.class)))
                .thenReturn("");

        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken =
                new UsernamePasswordAuthenticationToken("username", "password");

        // Assert
        Assertions.assertThatExceptionOfType(InsufficientAuthenticationException.class)
                .isThrownBy(() -> requestAuthenticationProvider.authenticate(usernamePasswordAuthenticationToken));
    }

    @Test
    void authenticate_withNoAuthorities_throwsInsufficientAuthenticationException() {
        // Arrange
        UserDto userDto = new UserDto();
        userDto.setPassword("password");
        userDto.setAuthorities(Collections.emptyList());

        Mockito.when(userService.loadUserByUsername(ArgumentMatchers.anyString()))
                .thenReturn(userDto);

        Mockito.when(passwordEncoder.matches(ArgumentMatchers.anyString(), ArgumentMatchers.anyString()))
                .thenReturn(true);

        Mockito.when(messageSource.getMessage(ArgumentMatchers.eq("authentication.exception.insufficient-roles"), ArgumentMatchers.any(Object[].class), ArgumentMatchers.any(Locale.class)))
                .thenReturn("");

        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken =
                new UsernamePasswordAuthenticationToken("username", "password");

        // Assert
        Assertions.assertThatExceptionOfType(InsufficientAuthenticationException.class)
                .isThrownBy(() -> requestAuthenticationProvider.authenticate(usernamePasswordAuthenticationToken));
    }

    @Test
    void authenticate_withValidCredentialsAndAuthorities_returnsUsernameAndPasswordAuthenticationTokenWithUserDetails() {
        // Arrange
        List<GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("ROLE_TEST_USER"));

        UserDto userDto = new UserDto();
        userDto.setId(1L);
        userDto.setPassword("password");
        userDto.setVerified(true);
        userDto.setAuthorities(authorities);

        Mockito.when(userService.loadUserByUsername(ArgumentMatchers.anyString()))
                .thenReturn(userDto);

        Mockito.when(passwordEncoder.matches(ArgumentMatchers.anyString(), ArgumentMatchers.anyString()))
                .thenReturn(true);

        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken =
                new UsernamePasswordAuthenticationToken("username", "password");

        // Act
        UsernamePasswordAuthenticationToken result =
                (UsernamePasswordAuthenticationToken) requestAuthenticationProvider.authenticate(usernamePasswordAuthenticationToken);

        // Assert
        UserContext userContext = (UserContext) result.getPrincipal();

        Assertions.assertThat(userContext.getUserId()).isEqualTo(userDto.getId());
        Assertions.assertThat(userContext.getUsername()).isEqualTo(userDto.getUsername());
        Assertions.assertThat(userContext.isVerified()).isTrue();
        Assertions.assertThat(userContext.getAuthorities()).isEqualTo(authorities);
        Assertions.assertThat(result.getCredentials()).isNull();
        Assertions.assertThat(result.getAuthorities()).isEqualTo(authorities);
    }

    @Test
    void supports_withNonUsernamePasswordAuthenticationToken_returnsFalse() {
        // Act
        boolean result = requestAuthenticationProvider.supports(Integer.class);

        // Assert
        Assertions.assertThat(result).isFalse();
    }

    @Test
    void supports_withUsernamePasswordAuthenticationToken_returnsTrue() {
        // Act
        boolean result = requestAuthenticationProvider.supports(UsernamePasswordAuthenticationToken.class);

        // Assert
        Assertions.assertThat(result).isTrue();
    }
}

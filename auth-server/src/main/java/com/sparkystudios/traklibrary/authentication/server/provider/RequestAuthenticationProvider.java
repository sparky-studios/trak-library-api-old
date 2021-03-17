package com.sparkystudios.traklibrary.authentication.server.provider;

import com.sparkystudios.traklibrary.authentication.service.dto.UserDto;
import com.sparkystudios.traklibrary.security.context.UserContext;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class RequestAuthenticationProvider implements AuthenticationProvider {

    private final UserDetailsService userDetailsService;
    private final PasswordEncoder passwordEncoder;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        // Retrieve the credentials passed into the token.
        String username = (String) authentication.getPrincipal();
        String password = (String) authentication.getCredentials();

        // Check to see if the credentials are linked to an existing user, if not it'll throw
        // a username not found exception.
        UserDto userDto = (UserDto)userDetailsService.loadUserByUsername(username);

        // Ensure the password is correct.
        if (!passwordEncoder.matches(password, userDto.getPassword())) {
            throw new BadCredentialsException("Authentication failed. Username or password not valid.");
        }

        // This should never happen, but highlights an issue where there are no defined roles in the
        // database.
        if (userDto.getAuthorities() == null || userDto.getAuthorities().isEmpty()) {
            throw new InsufficientAuthenticationException("User has no roles assigned");
        }

        // Convert the authorities into something that can be interpreted further down the chain.
        List<GrantedAuthority> authorities = userDto.getAuthorities().stream()
                .map(authority -> new SimpleGrantedAuthority(authority.getAuthority()))
                .collect(Collectors.toList());

        // Create the user context, which contains hand-picked user data to pass between services.
        UserContext userContext = new UserContext();
        userContext.setUserId(userDto.getId());
        userContext.setUsername(userDto.getUsername());
        userContext.setVerified(userDto.isVerified());
        userContext.setAuthorities(authorities);

        // Create another username and password token with authority information that can be
        // responded to further down the chain.
        return new UsernamePasswordAuthenticationToken(userContext, null, authorities);
    }

    @Override
    public boolean supports(Class<?> aClass) {
        return UsernamePasswordAuthenticationToken.class.isAssignableFrom(aClass);
    }
}

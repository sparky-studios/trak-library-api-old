package com.sparkystudios.traklibrary.security.provider;

import com.sparkystudios.traklibrary.security.token.SecurityTokenService;
import com.sparkystudios.traklibrary.security.token.authentication.JwtAuthenticationToken;
import com.sparkystudios.traklibrary.security.token.data.SecurityToken;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collection;

/**
 * The {@link JwtAuthenticationProvider} is an authentication provider that is used by any micro-service
 * that protects resources behind authentication that requires a JWT. Its purpose is to perform validation
 * and authorization whenever a JWT bearer token is provided as the authorization header for any request.
 *
 * @author Sparky Studios
 * @since 0.1.0
 */
@Component
@RequiredArgsConstructor
public class JwtAuthenticationProvider implements AuthenticationProvider {

    private final SecurityTokenService securityTokenService;

    /**
     * Checks whether the credentials contained within the {@link Authentication} contains a valid JWT
     * that has not expired or been malformed. If JWT parsing is successful, it will ensure that the token
     * provided is not a refresh token, before generating a new {@link JwtAuthenticationToken} and populating
     * it with additional authorization data.
     *
     * If the JWT is expired or malformed, a {@link io.jsonwebtoken.JwtException} exception will be bubbled up
     * the stack is thrown and if the authorities provided within the JWT contain TOKEN_REFRESH,a
     * {@link InsufficientAuthenticationException} exception is thrown and bubbled up the stack.
     *
     * @param authentication The {@link JwtAuthenticationToken} to attempt authentication with.
     *
     * @return A {@link JwtAuthenticationToken} with additional user data.
     *
     * @throws AuthenticationException Thrown if authentication fails or an invalid JWT is provided.
     */
    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        // The credentials should be the JWT as the authentication should be a JwtAuthenticationToken
        // instance.
        String accessToken = (String) authentication.getCredentials();
        SecurityToken securityToken = securityTokenService.getToken(accessToken);

        Collection<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(securityToken.getRole());
        authorities.addAll(securityToken.getAuthorities());

        return new JwtAuthenticationToken(securityToken, authorities);
    }

    /**
     * Flags that that the {@link JwtAuthenticationProvider} will only be executed if the authentication
     * type provided is assignable to a {@link JwtAuthenticationToken}.
     *
     * @param aClass The class type to check for assignability.
     *
     * @return True if the argument is assignable to a {@link JwtAuthenticationToken}.
     */
    @Override
    public boolean supports(Class<?> aClass) {
        return JwtAuthenticationToken.class.isAssignableFrom(aClass);
    }
}

package com.sparkystudios.traklibrary.security.provider;

import com.sparkystudios.traklibrary.security.context.UserContext;
import com.sparkystudios.traklibrary.security.token.JwtAuthenticationToken;
import io.jsonwebtoken.Jwts;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

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

    @Value("${trak.security.jwt.secret-key}")
    private String secretKey;

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

        // Try to parse the JWT, if it fails an exception will be bubbled up the stack.
        var claims = Jwts.parser()
                .setSigningKey(secretKey.getBytes())
                .parseClaimsJws(accessToken)
                .getBody();

        String username = claims.getSubject();
        @SuppressWarnings("unchecked")
        List<String> scopes = (List<String>) claims.get("scopes");

        // Ensure that the authorization header token provided isn't a refresh token.
        if (scopes.contains("TOKEN_REFRESH")) {
            throw new InsufficientAuthenticationException("Refresh token cannot be used for authorization.");
        }

        // This is classed as successful authorization at this point, so just populate a token with data.
        List<SimpleGrantedAuthority> authorities = scopes
                .stream()
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());

        var userContext = new UserContext();
        userContext.setUserId(claims.get("userId", Long.class));
        userContext.setUsername(username);
        userContext.setVerified(claims.get("verified", Boolean.class));
        userContext.setAuthorities(authorities);

        return new JwtAuthenticationToken(userContext, authorities);
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

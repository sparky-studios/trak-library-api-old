package com.sparkystudios.traklibrary.security.provider;

import com.sparkystudios.traklibrary.security.context.UserContext;
import com.sparkystudios.traklibrary.security.token.JwtAuthenticationToken;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationProvider implements AuthenticationProvider {

    @Value("${trak.security.jwt.secret-key}")
    private String secretKey;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String accessToken = (String) authentication.getCredentials();

        Claims claims = Jwts.parser()
                .setSigningKey(secretKey.getBytes())
                .parseClaimsJws(accessToken)
                .getBody();

        String username = claims.getSubject();
        @SuppressWarnings("unchecked")
        List<SimpleGrantedAuthority> authorities = ((List<String>) claims.get("scopes"))
                .stream()
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());

        UserContext userContext = new UserContext();
        userContext.setUserId(claims.get("userId", Long.class));
        userContext.setUsername(username);
        userContext.setVerified(claims.get("verified", Boolean.class));
        userContext.setAuthorities(authorities);

        return new JwtAuthenticationToken(userContext, authorities);
    }

    @Override
    public boolean supports(Class<?> aClass) {
        return JwtAuthenticationToken.class.isAssignableFrom(aClass);
    }
}

package com.sparkystudios.traklibrary.gateway.server.filter;

import com.google.common.base.Strings;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class AuthenticationManager implements ReactiveAuthenticationManager {

    private final String secretKey;

    @Override
    public Mono<Authentication> authenticate(Authentication authentication) {

        // Get the current credentials, which should be the JWT at this stage.
        String token = authentication.getCredentials().toString();

        try {
            // Validate the token.
            Claims claims = Jwts.parser()
                    .setSigningKey(secretKey.getBytes())
                    .parseClaimsJws(token)
                    .getBody();

            String username = claims.getSubject();

            if (!Strings.isNullOrEmpty(username)) {
                @SuppressWarnings("unchecked")
                List<SimpleGrantedAuthority> authorities = ((List<String>) claims.get("authorities"))
                        .stream()
                        .map(SimpleGrantedAuthority::new)
                        .collect(Collectors.toList());

                // Create the authenticated user, which stores some details about the user that made the request.
                AuthenticatedUserDto authenticatedUserDto = new AuthenticatedUserDto();
                authenticatedUserDto.setUserId(claims.get("userId", Long.class));
                authenticatedUserDto.setVerified(claims.get("verified", Boolean.class));
                authenticatedUserDto.setToken(token);

                // Create the authenticated object, which includes the username and the authorities associated with the user.
                UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(username, null, authorities);
                auth.setDetails(authenticatedUserDto);

                // Authentication the user.
                SecurityContextHolder.getContext().setAuthentication(auth);

                return Mono.just(auth);
            }
        } catch (Exception e) {
            SecurityContextHolder.clearContext();
        }

        return Mono.empty();
    }

    @Data
    public static class AuthenticatedUserDto {

        private long userId;

        private boolean verified;

        private String token;
    }
}

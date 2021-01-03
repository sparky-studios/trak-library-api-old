package com.sparkystudios.traklibrary.gateway.server.filter;

import com.google.common.base.Strings;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.security.web.server.context.ServerSecurityContextRepository;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class SecurityContextRepository implements ServerSecurityContextRepository {

    private final AuthenticationManager authenticationManager;

    @Override
    public Mono<Void> save(ServerWebExchange serverWebExchange, SecurityContext securityContext) {
        throw new UnsupportedOperationException("Not supported by security context.");
    }

    @Override
    public Mono<SecurityContext> load(ServerWebExchange swe) {
        ServerHttpRequest request = swe.getRequest();

        // Grab the auth header from the request.
        String header = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);

        // If no authorization has been provided, just carry on down the request filter chain.
        if (Strings.isNullOrEmpty(header) || !header.startsWith("Bearer")) {
            return Mono.empty();
        }

        // If no token if provided the user won't be authenticated, but at this point they may be trying to access a publicly accessible path.
        String token = header.replace("Bearer", "").trim();

        if (!Strings.isNullOrEmpty(token)) {
            // Create the authentication object, which at this stage only contains the token.
            Authentication auth = new UsernamePasswordAuthenticationToken(token, token);
            return authenticationManager.authenticate(auth).map(SecurityContextImpl::new);
        }

        return Mono.empty();
    }
}

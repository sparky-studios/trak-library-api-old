package com.sparkystudios.traklibrary.gateway.server.filter;

import com.google.common.base.Strings;
import com.sparkystudios.traklibrary.security.token.SecurityTokenService;
import com.sparkystudios.traklibrary.security.token.data.SecurityToken;
import com.sparkystudios.traklibrary.security.token.data.UserData;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.Collection;

@Slf4j
@RequiredArgsConstructor
public class AuthenticationManager implements ReactiveAuthenticationManager {

    private final SecurityTokenService securityTokenService;

    @Override
    public Mono<Authentication> authenticate(Authentication authentication) {

        // Get the current credentials, which should be the JWT at this stage.
        var token = authentication.getCredentials().toString();

        try {
            // Retrieve and validate the token.
            SecurityToken securityToken = securityTokenService.getToken(token);

            String username = securityToken.getUsername();

            if (!Strings.isNullOrEmpty(username)) {
                Collection<GrantedAuthority> authorities = new ArrayList<>();
                authorities.add(securityToken.getRole());
                authorities.addAll(securityToken.getAuthorities());

                // Create the authenticated user, which stores some details about the user that made the request.
                UserData userData = new UserData();
                userData.setUserId(securityToken.getUserId());
                userData.setUsername(username);
                userData.setVerified(securityToken.isVerified());
                userData.setUsing2fa(false);
                userData.setAuthorities(authorities);

                // Create the authenticated object, which includes the username and the authorities associated with the user.
                var auth = new UsernamePasswordAuthenticationToken(username, null, authorities);
                auth.setDetails(userData);

                // Authentication the user.
                SecurityContextHolder.getContext().setAuthentication(auth);

                return Mono.just(auth);
            }
        } catch (Exception e) {
            SecurityContextHolder.clearContext();
        }

        return Mono.empty();
    }
}

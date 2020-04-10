package com.sparky.trak.game.service.impl;

import com.sparky.trak.game.service.AuthenticationService;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class AuthenticationServiceImpl implements AuthenticationService {

    @Override
    public Authentication getAuthentication() {
        return SecurityContextHolder.getContext().getAuthentication();
    }

    @Override
    public boolean isCurrentAuthenticatedUser(long userId) {
        // We can't inject this, so it has to be grabbed directly from the current context.
        Authentication authentication = getAuthentication();

        // We only check the authentication if the principal provided is the one we're expecting for authentication.
        if (authentication.getPrincipal() instanceof UsernamePasswordAuthenticationToken) {
            UsernamePasswordAuthenticationToken token = (UsernamePasswordAuthenticationToken)authentication.getPrincipal();
            return Long.parseLong(token.getDetails().toString()) == userId;
        }

        return false;
    }
}

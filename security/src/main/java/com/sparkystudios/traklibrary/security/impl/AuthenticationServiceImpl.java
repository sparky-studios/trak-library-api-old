package com.sparkystudios.traklibrary.security.impl;

import com.sparkystudios.traklibrary.security.AuthenticationService;
import com.sparkystudios.traklibrary.security.context.UserContext;
import com.sparkystudios.traklibrary.security.token.JwtAuthenticationToken;
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
    public String getToken() {
        // We can't inject this, so it has to be grabbed directly from the current context.
        var authentication = getAuthentication();
        // We only check the authentication if the principal provided is the one we're expecting for authentication.
        return authentication instanceof JwtAuthenticationToken ? (String) authentication.getCredentials() : "";
    }

    @Override
    public boolean isCurrentAuthenticatedUser(long userId) {
        // We can't inject this, so it has to be grabbed directly from the current context.
        var authentication = getAuthentication();

        // We only check the authentication if the principal provided is the one we're expecting for authentication.
        if (authentication instanceof JwtAuthenticationToken) {
            JwtAuthenticationToken token = (JwtAuthenticationToken)authentication;

            // If the user calling the end-point has an admin role, authenticate them so that they can edit any data.
            boolean isAdmin = token.getAuthorities()
                    .stream()
                    .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN"));

            // Get the authenticated user details.
            var userContext = (UserContext) token.getPrincipal();
            return isAdmin || userContext.getUserId() == userId;
        }

        return false;
    }
}
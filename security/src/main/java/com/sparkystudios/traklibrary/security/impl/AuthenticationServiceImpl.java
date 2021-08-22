package com.sparkystudios.traklibrary.security.impl;

import com.sparkystudios.traklibrary.security.AuthenticationService;
import com.sparkystudios.traklibrary.security.token.authentication.JwtAuthenticationToken;
import com.sparkystudios.traklibrary.security.token.data.SecurityToken;
import com.sparkystudios.traklibrary.security.token.data.UserSecurityRole;
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
            JwtAuthenticationToken token = (JwtAuthenticationToken) authentication;
            SecurityToken securityToken = (SecurityToken) token.getPrincipal();

            // If the user calling the end-point has an admin role, authenticate them so that they can edit any data.
            boolean isAdmin = securityToken.getRole().getAuthority().equals(UserSecurityRole.ROLE_ADMIN.name());

            // Get the authenticated user details.
            var userData = (SecurityToken) token.getPrincipal();
            return isAdmin || userData.getUserId() == userId;
        }

        return false;
    }
}
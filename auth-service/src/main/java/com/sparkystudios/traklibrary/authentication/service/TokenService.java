package com.sparkystudios.traklibrary.authentication.service;

import com.sparkystudios.traklibrary.security.context.UserContext;

public interface TokenService {

    /**
     * Creates a new access token which will allow the user authenticated access to the application,
     * depending on the scopes and permissions assigned to the authenticated user. By default, the access
     * token will have a expiry time of 15 minutes so will need to be refresh when a 401 is returned.
     *
     * @param userContext The {@link UserContext} instance to retrieve user data from.
     *
     * @return A new access token with user information and scopes provided.
     */
    String createAccessToken(UserContext userContext, String role, Iterable<String> scopes);

    /**
     * Creates a new refresh token which will allow the user to refresh an expired access token by provided
     * this refresh token alongside it. Although the refresh token is longer lived than an access token, it
     * contains less authorities and can only be used for refresh purposes.
     *
     * @param userContext The {@link UserContext} instance to retrieve user data from.
     *
     * @return A new refresh token with user information and refresh scope provided.
     */
    String createRefreshToken(UserContext userContext);
}

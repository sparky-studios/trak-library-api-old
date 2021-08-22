package com.sparkystudios.traklibrary.security.token;

import com.sparkystudios.traklibrary.security.token.data.SecurityToken;
import com.sparkystudios.traklibrary.security.token.data.UserData;
import com.sparkystudios.traklibrary.security.token.data.UserSecurityRole;

/**
 * The {@link SecurityTokenService} is a simple interface service that is responsible for
 * creating security tokens of different types and scopes, such as authorization and refreshing.
 *
 * @author Sparky Studios.
 */
public interface SecurityTokenService {

    /**
     * Retrieves data about the given token as a {@link SecurityToken}. If the token passed in is invalid,
     * an exception will be thrown with additional information.
     *
     * @param token The token to retrieve data from.
     *
     * @return The token information, as a {@link SecurityToken} instance.
     */
    SecurityToken getToken(String token);

    /**
     * Creates a new access token which will allow the user authenticated access to the application,
     * depending on the scopes and permissions assigned to the authenticated user. By default, the access
     * token will have an expiry time of 15 minutes so will need to be refreshed when a 401 is returned.
     *
     * @param userData The {@link UserData} instance to retrieve user data from.
     *
     * @return A new access token with user information and scopes provided.
     */
    SecurityToken createAccessToken(UserData userData, UserSecurityRole userSecurityRole, Iterable<String> scopes);

    /**
     * Creates a new token that is used when the user has correct credentials but has two-factor
     * authentication enabled on their account. Similar to the refresh token, they will be assigned a single
     * role which will prevent to all but a single endpoint.
     *
     * @param userData The {@link UserData} instance to retrieve user data from.
     *
     * @return A new two-factor authentication token with user information provided.
     */
    SecurityToken createTwoFactorAuthenticationToken(UserData userData);

    /**
     * Creates a new refresh token which will allow the user to refresh an expired access token by provided
     * this refresh token alongside it. Although the refresh token is longer lived than an access token, it
     * contains fewer authorities and can only be used for refresh purposes.
     *
     * @param userData The {@link UserData} instance to retrieve user data from.
     *
     * @return A new refresh token with user information and refresh scope provided.
     */
    SecurityToken createRefreshToken(UserData userData);
}

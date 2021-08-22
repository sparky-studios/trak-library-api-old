package com.sparkystudios.traklibrary.security.token.data;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Enum that represents all the different user security roles that can be assigned to a given user.
 * The role helps define the high-level permissions that an individual user has.
 *
 * @author Sparky Studios
 */
@Getter
@RequiredArgsConstructor
public enum UserSecurityRole {

    /**
     * Represents and maps to the ROLE_USER in the underlying database. This permission will be used for most users
     * of the system and limits the scopes of their changes to their own accounts and libraries. A User cannot create
     * new data outside themselves, such as games or developers, they instead can make requests for such elements
     * to be added.
     */
    ROLE_USER(1),
    ROLE_MODERATOR(2),
    ROLE_ADMIN(3),
    ROLE_TWO_FACTOR_AUTHENTICATION_TOKEN(4),
    ROLE_TOKEN_REFRESH(5);

    private final long id;
}

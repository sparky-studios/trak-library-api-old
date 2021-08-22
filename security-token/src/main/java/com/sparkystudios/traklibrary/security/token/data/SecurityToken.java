package com.sparkystudios.traklibrary.security.token.data;

import org.springframework.security.core.GrantedAuthority;

import java.time.LocalDateTime;
import java.util.Collection;

/**
 * The {@link SecurityToken} is a simple interface that represents a token used for authorisation
 * throughout the system, whether this be with JWT or another implementation. Its purpose is to
 * map information from different providers into a contract that can be used across different services
 * and webapps.
 *
 * @author Sparky Studios
 */
public interface SecurityToken {

    /**
     * Retrieves the unique ID that has been generated for this given {@link SecurityToken}.
     *
     * @return The unique ID of the {@link SecurityToken}.
     */
    String getId();

    /**
     * Retrieves the raw token data that was initially set and is used to retrieve security information from.
     *
     * @return The raw token that is used to retrieve information from.
     */
    String getToken();

    /**
     * Retrieves the token type associated with this {@link SecurityToken}. This is commonly what the prefix will
     * be when using this token for authorisation purposes, such as "Bearer".
     *
     * @return The type of the {@link SecurityToken}.
     */
    String getType();

    /**
     * Retrieves the username of the user that is associated with this {@link SecurityToken}. The
     * username refers to their unique username that they assigned to themselves during registration,
     * it contains no reference to their email address or other personal information.
     *
     * @return The username of the user that is associated with this {@link SecurityToken}.
     */
    String getUsername();

    /**
     * Retrieves the unique ID of the user that is associated with this {@link SecurityToken}.
     *
     * @return The unique ID of the user associated with this {@link SecurityToken}.
     */
    long getUserId();

    /**
     * Retrieves the current verification state of the given token. Although verification does
     * not immediately restrict access to the system, their access will be disabled after 24
     * hours if they have not yet verified their account.
     *
     * @return The current verification state of the {@link SecurityToken}.
     */
    boolean isVerified();

    /**
     * Retrieves as a {@link LocalDateTime} with a UTC timezone when the {@link SecurityToken} was
     * initially issued. For most security tokens, this will be no greater than 15 minutes ago, unless
     * it's a token for a specific purpose, such as refreshing or two-factor authentication.
     *
     * @return The issue date of the {@link SecurityToken}.
     */
    LocalDateTime getIssuedAt();

    /**
     * Retrieves as a {@link LocalDateTime} with a UTC timezone when the {@link SecurityToken} will
     * expire. For most security token, this will be no greater than 15 minutes after the issue date,
     * unless it's a token for a specific purpose, such as refreshing or two-factor authentication.
     *
     * @return The expiry time of the {@link SecurityToken}.
     */
    LocalDateTime getExpiry();

    /**
     * Retrieves the role assigned to the given token as a {@link GrantedAuthority}. The role
     * for the token will most commonly denote the high-level access that the user has, such as
     * admin, moderator or user access.
     *
     * @return The current role of the token, wrapped within a {@link GrantedAuthority}.
     */
    GrantedAuthority getRole();

    /**
     * Retrieve each authority of the token, as a {@link Collection} of {@link GrantedAuthority}
     * instances. The authorities of a token define the different levels of access or permissions that
     * the user has to the system, such as whether they can save, modify or delete specific sub-sets
     * of data.
     *
     * @return A {@link Collection} of current authorities, wrapped as {@link GrantedAuthority} instances.
     */
    Collection<GrantedAuthority> getAuthorities();
}

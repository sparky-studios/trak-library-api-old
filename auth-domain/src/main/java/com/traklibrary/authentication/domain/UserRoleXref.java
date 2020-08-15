package com.traklibrary.authentication.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;

/**
 * Represents a many-to-many relationship between a {@link User} and the {@link UserRole}'s that is has been
 * assigned. Every {@link User} within the system will be assigned at least one {@link UserRole}, most commonly
 * being just a standard Trak user with no elevated privileges.
 *
 * @author Sparky Studios.
 */
@Data
@Entity
@Table(name = "auth_user_role_xref")
public class UserRoleXref {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", unique = true, nullable = false, updatable = false)
    private long id;

    @Column(name = "auth_user_id", nullable = false, updatable = false)
    private long userId;

    @EqualsAndHashCode.Exclude
    @ManyToOne(fetch = FetchType.LAZY, cascade = { CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH })
    @JoinColumn(name = "auth_user_id", updatable = false, insertable = false)
    private User user;

    @Column(name = "auth_user_role_id", nullable = false, updatable = false)
    private long userRoleId;

    @EqualsAndHashCode.Exclude
    @ManyToOne(fetch = FetchType.LAZY, cascade = { CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH })
    @JoinColumn(name = "auth_user_role_id", updatable = false, insertable = false)
    private UserRole userRole;
}

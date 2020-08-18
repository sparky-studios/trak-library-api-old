package com.traklibrary.authentication.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;
import java.util.Set;

/**
 * The {@link UserRole} entity is used to represent all of the different user roles that can be assigned to a
 * user. These {@link UserRole}'s can only be accessed and manipulated by a user with administrator access.
 * A {@link User} does not have direct access to a {@link UserRole}, this table only represents the roles that
 * are available to users, the connection between the two tables is done via {@link UserRole} entities.
 *
 * The purpose of {@link UserRole}'s to represent the level of access that each {@link User} will have to the
 * system, most commonly they will only be a basic user, however others may be granted elevated or administrator
 * access to the system.
 *
 * @author Sparky Studios.
 */
@Data
@Entity
@Table(name = "auth_user_role")
public class UserRole {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", unique = true, nullable = false, updatable = false)
    private long id;

    @Column(name = "role", nullable = false, unique = true, length = 30)
    private String role;

    @EqualsAndHashCode.Exclude
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "userRole", cascade = CascadeType.ALL)
    private Set<UserRoleXref> userRoleXrefs;

    @Version
    @Column(name = "op_lock_version")
    private Long version;
}

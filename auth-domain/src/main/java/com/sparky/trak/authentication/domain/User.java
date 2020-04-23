package com.sparky.trak.authentication.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

/**
 * The {@link User} entity represents a record in the user table within the auth database. Its purpose
 * is to reflect the login information and high-level credentials for each individual user that has signed up to
 * Trak. It should be noted that although password information is provided, it is encrypted with BCrypt before
 * being persisted within the database so is not personally identifiable.
 *
 * Every user of the system, be it an admin, moderator or just a standard user will have credentials stored within
 * this table.
 *
 * @author Sparky Studios
 */
@Data
@Entity
@Table(name = "auth_user")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", unique = true, nullable = false, updatable = false)
    private long id;

    @Column(name = "username", nullable = false)
    private String username;

    @Column(name = "email_address", nullable = false)
    private String emailAddress;

    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "verified", nullable = false)
    private boolean verified;

    @Column(name = "verification_code")
    private Short verificationCode;

    @EqualsAndHashCode.Exclude
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "user", cascade = CascadeType.ALL)
    private Set<UserRoleXref> userRoleXrefs = new HashSet<>();

    @Version
    @Column(name = "op_lock_version")
    private Long version;
}

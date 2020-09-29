package com.sparkystudios.traklibrary.authentication.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

/**
 * The {@link User} entity represents a record in the auth_user table within the auth database. Its purpose
 * is to reflect the login information and high-level credentials for each individual user that has signed up to
 * Trak. It should be noted that although password information is provided, it is encrypted with BCrypt before
 * being persisted within the database so is not personally identifiable.
 * <p>
 * Every user of the system, be it an admin, moderator or just a standard user will have credentials stored within
 * this table.
 *
 * @author Sparky Studios
 */
@Data
@Entity
@EntityListeners(AuditingEntityListener.class)
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

    @Column(name = "verification_code", length = 5)
    private String verificationCode;

    @Column(name = "verification_expiry_date")
    private LocalDateTime verificationExpiryDate;

    @Column(name = "recovery_token", length = 30)
    private String recoveryToken;

    @Column(name = "recovery_token_expiry_date")
    private LocalDateTime recoveryTokenExpiryDate;

    @EqualsAndHashCode.Exclude
    @ManyToMany(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST,CascadeType.MERGE,CascadeType.DETACH})
    @JoinTable(
            name = "auth_user_role_xref",
            joinColumns = {@JoinColumn(name = "auth_user_id")},
            inverseJoinColumns = {@JoinColumn(name = "auth_user_role_id")}
    )
    private Set<UserRole> userRoles = new HashSet<>();

    @Column(name = "created_at", nullable = false, updatable = false)
    @CreatedDate
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    @LastModifiedDate
    private LocalDateTime updatedAt;

    @Version
    @Column(name = "op_lock_version")
    private Long version;

    /**
     * Convenience method that is used to add a {@link UserRole} to the {@link User}. As
     * the relationship between the {@link User} and {@link UserRole} is bi-directional,
     * it needs to be added and associated with on both sides of the relationship, which
     * this method achieved.
     *
     * @param userRole The {@link UserRole} to add to the {@link User}.
     */
    public void addUserRole(UserRole userRole) {
        userRoles.add(userRole);
        userRole.getUsers().add(this);
    }

    /**
     * Convenience method that is used to remove a {@link UserRole} to the {@link User}. As
     * the relationship between the {@link User} and {@link UserRole} is bi-directional,
     * it needs to be added and associated with on both sides of the relationship, which
     * this method achieved.
     *
     * @param userRole The {@link UserRole} to remove to the {@link User}.
     */
    public void removeUserRole(UserRole userRole) {
        userRoles.remove(userRole);
        userRole.getUsers().remove(this);
    }
}

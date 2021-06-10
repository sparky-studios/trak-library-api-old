package com.sparkystudios.traklibrary.authentication.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

/**
 * The {@link UserRole} entity is used to represent an individual role that can be assigned to a
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
@EntityListeners(AuditingEntityListener.class)
@Table(name = "auth_user_role")
public class UserRole {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", unique = true, nullable = false, updatable = false)
    private long id;

    @Column(name = "role", nullable = false, unique = true, length = 30)
    private String role;

    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @OneToMany(mappedBy = "userRole", fetch = FetchType.LAZY)
    private Set<User> users = new HashSet<>();

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
     * Convenience method that is used to add a {@link User} to the {@link UserRole}. As
     * the relationship between the {@link User} and {@link UserRole} is bi-directional,
     * it needs to be added and associated with on both sides of the relationship, which
     * this method achieved.
     *
     * @param user The {@link User} to add to the {@link UserRole}.
     */
    public void addUser(User user) {
        users.add(user);
        user.setUserRole(this);
    }

    /**
     * Convenience method that is used to remove a {@link User} to the {@link UserRole}. As
     * the relationship between the {@link User} and {@link UserRole} is bi-directional,
     * it needs to be added and associated with on both sides of the relationship, which
     * this method achieved.
     *
     * @param user The {@link User} to remove to the {@link UserRole}.
     */
    public void removeUser(User user) {
        users.remove(user);
        user.setUserRole(null);
    }
}

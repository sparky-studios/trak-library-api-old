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
 * The {@link UserAuthority} entity is used to represent an individual authority that can be assigned to a
 * user. These {@link UserAuthority}'s can only be accessed and manipulated by a user with administrator access.
 * A {@link User} does not have direct access to a {@link UserAuthority}, this table only represents the authorities that
 * are available to users, the connection between the two tables is done via a conjunction table.
 *
 * The purpose of {@link UserAuthority}'s to represent the granular level of access that each {@link User} will have to the
 * system, most commonly they will only be a basic user, however others may be granted elevated or administrator
 * access to the system.
 *
 * @author Sparky Studios.
 */
@Data
@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "auth_user_authority")
public class UserAuthority {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", unique = true, nullable = false, updatable = false)
    private long id;

    @Column(name = "authority", nullable = false, unique = true)
    private String authority;

    @Column(name = "feature", nullable = false, updatable = false)
    private Feature feature;

    @Column(name = "authority_type", nullable = false, updatable = false)
    private AuthorityType authorityType;

    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @ManyToMany(mappedBy = "authorities", cascade = {CascadeType.PERSIST,CascadeType.MERGE,CascadeType.DETACH}, fetch = FetchType.LAZY)
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
     * Convenience method that is used to add a {@link User} to the {@link UserAuthority}. As
     * the relationship between the {@link User} and {@link UserAuthority} is bi-directional,
     * it needs to be added and associated with on both sides of the relationship, which
     * this method achieved.
     *
     * @param user The {@link User} to add to the {@link UserAuthority}.
     */
    public void addUser(User user) {
        users.add(user);
        user.getAuthorities().add(this);
    }

    /**
     * Convenience method that is used to remove a {@link User} to the {@link UserAuthority}. As
     * the relationship between the {@link User} and {@link UserAuthority} is bi-directional,
     * it needs to be added and associated with on both sides of the relationship, which
     * this method achieved.
     *
     * @param user The {@link User} to remove from the {@link UserAuthority}.
     */
    public void removeUser(User user) {
        users.remove(user);
        user.getAuthorities().remove(this);
    }
}

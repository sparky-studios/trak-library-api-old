package com.sparkystudios.traklibrary.game.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Data
@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "genre")
public class Genre {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", unique = true, nullable = false, updatable = false)
    private long id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "description", length = 4096)
    private String description;

    @EqualsAndHashCode.Exclude
    @ManyToMany(mappedBy = "genres", cascade = {CascadeType.PERSIST,CascadeType.MERGE,CascadeType.DETACH}, fetch = FetchType.LAZY)
    private Set<Game> games = new HashSet<>();

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
     * Convenience method that is used to add a {@link Game} to the {@link Genre}. As
     * the relationship between the {@link Genre} and {@link Game} is bi-directional,
     * it needs to be added and associated with on both sides of the relationship, which
     * this method achieved.
     *
     * @param game The {@link Game} to add to the {@link Genre}.
     */
    public void addGame(Game game) {
        games.add(game);
        game.getGenres().add(this);
    }

    /**
     * Convenience method that is used to remove a {@link Game} to the {@link Genre}. As
     * the relationship between the {@link Genre} and {@link Game} is bi-directional,
     * it needs to be added and associated with on both sides of the relationship, which
     * this method achieved.
     *
     * @param game The {@link Game} to remove to the {@link Genre}.
     */
    public void removeGame(Game game) {
        games.remove(game);
        game.getGenres().remove(this);
    }
}

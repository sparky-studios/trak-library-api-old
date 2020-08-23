package com.traklibrary.game.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Data
@Entity
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
    @ManyToMany(mappedBy = "genres", cascade = CascadeType.PERSIST, fetch = FetchType.LAZY)
    private Set<Game> games = new HashSet<>();

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
